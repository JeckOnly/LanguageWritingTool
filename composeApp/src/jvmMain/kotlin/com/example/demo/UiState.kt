package com.example.demo

import com.example.demo.data.AiConfigStore
import com.example.demo.domain.CheckEnglishUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val baseUrl: String = "http://localhost:1234/v1", // LM Studio 默认:contentReference[oaicite:6]{index=6}
    val model: String = "qwen2.5-vl-3b-instruct",
    val contextText: String = "",
    val draftText: String = "",
    val mode: CheckMode = CheckMode.RewriteNatural,

    val isLoading: Boolean = false,
    val result: CorrectionResult? = null,
    val error: String? = null
)

class MainViewModel(
    private val useCase: CheckEnglishUseCase,
    private val configStore: AiConfigStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun setBaseUrl(v: String) = configStore.update { it.copy(baseUrl = v) }
    fun setModel(v: String) = configStore.update { it.copy(model = v) }

    fun setContext(v: String) = _state.update { it.copy(contextText = v) }

    fun setDraft(v: String) {
        // 用户继续输入时，自动取消正在跑的请求（避免浪费/卡 UI）
        if (_state.value.isLoading) cancel()
        _state.update { it.copy(draftText = v) }
    }

    fun setMode(m: CheckMode) = _state.update { it.copy(mode = m) }

    fun cancel() {
        job?.cancel()
        job = null
        _state.update { it.copy(isLoading = false) }
    }

    fun checkNow() {
        job?.cancel()
        val s = _state.value
        job = scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
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

    fun applyRewriteToDraft() {
        val rewritten = _state.value.result?.rewritten?.trim().orEmpty()
        if (rewritten.isNotEmpty()) {
            _state.update { it.copy(draftText = rewritten) }
        }
    }
}
