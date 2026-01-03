package com.example.demo

interface AiGateway {
    suspend fun checkEnglish(
        contextText: String,
        draftText: String,
        mode: CheckMode = CheckMode.RewriteNatural
    ): CorrectionResult
}

enum class CheckMode { RewriteNatural, RewriteFormal, RewriteConcise }
