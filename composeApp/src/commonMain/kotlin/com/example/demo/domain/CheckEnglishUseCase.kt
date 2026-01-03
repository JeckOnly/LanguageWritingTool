package com.example.demo.domain

import com.example.demo.AiGateway
import com.example.demo.CheckMode
import com.example.demo.CorrectionResult

class CheckEnglishUseCase(
    private val gateway: AiGateway
) {
    suspend fun run(contextText: String, draftText: String, mode: CheckMode): CorrectionResult {
        if (draftText.isBlank()) {
            return CorrectionResult(rewritten = "", alternatives = emptyList(), notes = listOf("Draft is empty."))
        }
        return gateway.checkEnglish(contextText, draftText, mode)
    }
}