package com.example.demo.data

import com.example.demo.data.model.CorrectionResultData
import com.example.demo.domain.data.CorrectionPayload
import com.example.demo.domain.data.CorrectionResultDomain
import kotlinx.serialization.json.Json

object JsonExtract {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseCorrectionResult(modelText: String): CorrectionResultData {
        // 很多本地模型偶尔会“夹带”一些说明文字，这里做个容错：截取第一个 { 到最后一个 }。
        val trimmed = modelText.trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        require(start in 0..<end) { "Model did not return JSON. Raw: $trimmed" }

        val jsonText = trimmed.substring(start, end + 1)
        val payload = json.decodeFromString<CorrectionPayload>(CorrectionPayload.serializer(), jsonText)
        return CorrectionResultData(
            rewritten = payload.rewritten,
            alternatives = payload.alternatives,
        )
    }
}