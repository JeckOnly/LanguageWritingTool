package com.example.demo.domain.usecase

import com.example.demo.data.model.CorrectionResultData
import com.example.demo.data.model.DataResult
import com.example.demo.data.repo.AiGateway
import com.example.demo.data.repo.CEEnglishDataError
import com.example.demo.data.repo.CheckMode
import com.example.demo.domain.data.CorrectionResultDomain
import com.example.demo.domain.model.FlowUseCase
import com.example.demo.domain.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckEnglishUseCase(
    private val gateway: AiGateway,
    private val dispatcher: CoroutineDispatcher
): FlowUseCase<CheckEnglishParameter, CESuccess, CheckEnglishBusinessError>(dispatcher){
    override fun execute(parameters: CheckEnglishParameter): Flow<Result<CESuccess, CheckEnglishBusinessError>> {
        return flow {
            emit(Result.Loading)
            if (parameters.draftText.isBlank()) {
                emit(Result.BusinessRuleError(CheckEnglishBusinessError.CEDraftIsEmpty))
                return@flow
            }
            val result = gateway.checkEnglish(parameters.contextText, parameters.draftText, parameters.mode)
            when (result) {
                is DataResult.Error<CEEnglishDataError> -> {
                    when (result.error) {
                        is CEEnglishDataError.CECommonException -> throw result.error.e
                        is CEEnglishDataError.NetworkException -> emit(Result.BusinessRuleError(
                            CheckEnglishBusinessError.CENetworkError(
                                statusCode = result.error.statusCode,
                                description = result.error.description,
                                body = result.error.resBody
                            )))
                    }
                }
                is DataResult.Success<CorrectionResultData> -> {
                    emit(Result.Success(CESuccess(CorrectionResultDomain(
                        rewritten = result.data.rewritten,
                        alternatives = result.data.alternatives,
                    ))))
                }
            }



        }
    }
}

data class CheckEnglishParameter(val contextText: String, val draftText: String, val mode: CheckMode)

sealed class CheckEnglishBusinessError {
    data object CEDraftIsEmpty : CheckEnglishBusinessError()
    data class CENetworkError(val statusCode: Int, val description: String, val body: String) : CheckEnglishBusinessError()
}

data class CESuccess(val correctionResultDomain: CorrectionResultDomain)