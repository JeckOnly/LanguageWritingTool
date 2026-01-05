package com.example.demo.state

import com.example.demo.CheckMode
import com.example.demo.CorrectionResult

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