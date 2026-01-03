package com.example.demo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CorrectionResult(
    val rewritten: String,
    val alternatives: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)

/** 期望模型输出的 JSON 结构 */
@Serializable
internal data class CorrectionPayload(
    val rewritten: String,
    val alternatives: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)
