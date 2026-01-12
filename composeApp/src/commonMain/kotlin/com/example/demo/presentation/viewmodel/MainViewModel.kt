package com.example.demo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.data.repo.CheckMode
import com.example.demo.domain.model.Result
import com.example.demo.domain.usecase.CESuccess
import com.example.demo.domain.usecase.CheckEnglishBusinessError
import com.example.demo.domain.usecase.CheckEnglishParameter
import com.example.demo.presentation.AiConfigStore
import com.example.demo.domain.usecase.CheckEnglishUseCase
import com.example.demo.state.UiState
import kotlinx.coroutines.Job
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
    private val checkEnglishUseCase: CheckEnglishUseCase,
    private val configStore: AiConfigStore
) : ViewModel() {

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
        viewModelScope.launch {
            for (i in intents) {
                when (i) {
                    Intent.CheckNow -> checkCorrection()
                    Intent.Cancel -> doCancel(fromUser = true)
                }
            }
        }
    }

    // ----------------------------
    // 纯状态更新：直接 update
    // ----------------------------

    fun setBaseUrl(v: String)  {
        _state.update { it.copy(baseUrl = v) }
        configStore.update { it.copy(baseUrl = v) }
    }
    fun setModel(v: String) {
        _state.update { it.copy(model = v) }
        configStore.update { it.copy(model = v) }
    }

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
        val rewritten = _state.value.rewritten.trim()
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

    private fun checkCorrection() {
        checkJob?.cancel()

        val s = _state.value

        checkJob = viewModelScope.launch {
            checkEnglishUseCase(
                parameters = CheckEnglishParameter(
                    contextText = s.contextText,
                    draftText = s.draftText,
                    mode = s.mode
                )
            ).collect { r ->
                when (r) {
                    Result.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }

                    is Result.Success<*> -> {
                        val data = (r.data as CESuccess).correctionResultDomain
                        _state.update {
                            it.copy(
                                isLoading = false,
                                rewritten = data.rewritten,
                                alternatives = data.alternatives,
                                error = null
                            )
                        }
                    }

                    is Result.BusinessRuleError<*> -> {
                        val e = r.error as CheckEnglishBusinessError
                        val msg = when (e) {
                            CheckEnglishBusinessError.CEDraftIsEmpty ->
                                "Draft is empty."

                            is CheckEnglishBusinessError.CENetworkError -> {
                                val body = e.body.take(800) // 避免 UI 被超长 body 撑爆（可按需调整）
                                "Network error: statusCode=${e.statusCode}, description=${e.description}, body=$body"
                            }
                        }
                        _state.update { it.copy(isLoading = false, error = msg) }
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = r.error.message
                            )
                        }
                    }
                }
            }
        }
    }


    override fun onCleared() {
        // ViewModel 销毁时清理资源
        intents.close()
        checkJob?.cancel()
        super.onCleared()
    }
}
