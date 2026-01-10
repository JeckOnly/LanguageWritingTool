package com.example.demo.domain.data

import kotlinx.serialization.Serializable

@Serializable
data class CorrectionResultDomain(
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
