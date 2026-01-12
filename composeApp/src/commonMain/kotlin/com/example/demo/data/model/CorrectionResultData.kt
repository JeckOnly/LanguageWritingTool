package com.example.demo.data.model

import kotlinx.serialization.Serializable


@Serializable
data class CorrectionResultData(
    val rewritten: String,
    val alternatives: List<String> = emptyList(),
)