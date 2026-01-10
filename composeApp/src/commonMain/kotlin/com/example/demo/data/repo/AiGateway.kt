package com.example.demo.data.repo

import com.example.demo.data.model.CorrectionResultData
import com.example.demo.data.model.DataResult
import com.example.demo.domain.data.CorrectionResultDomain

interface AiGateway {
    suspend fun checkEnglish(
        contextText: String,
        draftText: String,
        mode: CheckMode = CheckMode.RewriteNatural
    ): DataResult<CorrectionResultData, CEEnglishDataError>
}

enum class CheckMode { RewriteNatural, RewriteFormal, RewriteConcise }
