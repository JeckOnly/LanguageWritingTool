package com.example.demo.state

import com.example.demo.data.repo.CheckMode
import com.example.demo.domain.data.CorrectionResultDomain

data class UiState(
    val baseUrl: String = "http://localhost:1234/v1",
    val model: String = "qwen2.5-vl-3b-instruct",
    val contextText: String = "",
    val draftText: String = "",
    val mode: CheckMode = CheckMode.RewriteNatural,

    val isLoading: Boolean = false,
    val rewritten: String = "",
    val alternatives: List<String> = emptyList(),
    val notes: List<String> = emptyList(),
    val error: String? = null
)