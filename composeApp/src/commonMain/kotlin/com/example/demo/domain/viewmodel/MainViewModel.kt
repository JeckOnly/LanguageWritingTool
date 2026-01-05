package com.example.demo.domain.viewmodel

import androidx.lifecycle.ViewModel
import com.example.demo.CheckMode
import com.example.demo.data.AiConfigStore
import com.example.demo.domain.CheckEnglishUseCase
import com.example.demo.state.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 *
 */
class MainViewModel(
    private val useCase: CheckEnglishUseCase,
    private val configStore: AiConfigStore
) : ViewModel() {

    /**
     * 仍然用你自己的 scope（避免 desktop 上 Main dispatcher 的依赖问题）
     * 记得在 onCleared 里 cancel。
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // 当前正在跑的 check job
    private var checkJob: Job? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    // 只放“命令类/副作用类”事件进 channel
    private sealed interface Intent {
        data object CheckNow : Intent
        data object Cancel : Intent
    }

    private val intents = Channel<Intent>(capacity = Channel.BUFFERED)

    init {
        // 单线程（逻辑上）消费 intent：保证 Check/Cancel 的顺序与一致性
        scope.launch {
            for (i in intents) {
                when (i) {
                    Intent.CheckNow -> doCheckNow()
                    Intent.Cancel -> doCancel(fromUser = true)
                }
            }
        }
    }

    // ----------------------------
    // 纯状态更新：直接 update
    // ----------------------------

    fun setBaseUrl(v: String) = configStore.update { it.copy(baseUrl = v) }
    fun setModel(v: String) = configStore.update { it.copy(model = v) }

    fun setContext(v: String) = _state.update { it.copy(contextText = v) }

    fun setDraft(v: String) {
        // 输入时：如果正在跑 check，立刻取消（不走 channel，避免输入延迟/浪费）
        if (_state.value.isLoading) {
            doCancel(fromUser = false)
        }
        _state.update { it.copy(draftText = v) }
    }

    fun setMode(m: CheckMode) = _state.update { it.copy(mode = m) }

    fun applyRewriteToDraft() {
        val rewritten = _state.value.result?.rewritten?.trim().orEmpty()
        if (rewritten.isNotEmpty()) {
            _state.update { it.copy(draftText = rewritten) }
        }
    }

    // ----------------------------
    // 命令类：进 channel（串行）
    // ----------------------------

    fun checkNow() {
        intents.trySend(Intent.CheckNow)
    }

    fun cancel() {
        intents.trySend(Intent.Cancel)
    }

    // ----------------------------
    // 内部实现：只在 scope/actor 内调用
    // ----------------------------

    private fun doCancel(fromUser: Boolean) {
        checkJob?.cancel()
        checkJob = null
        _state.update { it.copy(isLoading = false) }
        // 你也可以在这里打日志区分：用户点 cancel vs 输入导致 cancel
        // println("cancel: fromUser=$fromUser")
    }

    private fun doCheckNow() {
        // latest-wins：新 check 来了先取消旧的
        checkJob?.cancel()

        // 拍一个快照，避免中途 state 被输入修改影响本次请求
        val s = _state.value

        _state.update { it.copy(isLoading = true, error = null) }

        checkJob = scope.launch {
            try {
                val r = useCase.run(s.contextText, s.draftText, s.mode)
                _state.update { it.copy(isLoading = false, result = r) }
            } catch (e: CancellationException) {
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: e.toString()) }
            }
        }
    }

    override fun onCleared() {
        // ViewModel 销毁时清理资源
        intents.close()
        checkJob?.cancel()
        scope.coroutineContext.cancel() // 取消 scope（需要 import kotlinx.coroutines.cancel）
        super.onCleared()
    }
}
