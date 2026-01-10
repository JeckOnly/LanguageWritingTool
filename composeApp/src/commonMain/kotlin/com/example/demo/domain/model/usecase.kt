package com.example.demo.domain.model

import com.example.demo.AppError
import com.example.demo.mapToAppError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

abstract class FlowUseCase<in Parameters, Success, BusinessRuleError>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: Parameters): Flow<Result<Success, BusinessRuleError>> {
        return execute(parameters)
            .catch { e ->
//                Log.e("FlowUseCase", "An error occurred while executing the use case", e)
                emit(Result.Error(e.mapToAppError()))
            }
            .flowOn(dispatcher)
    }

    abstract fun execute(parameters: Parameters): Flow<Result<Success, BusinessRuleError>>
}

abstract class UseCase<in Parameters, Success, BusinessRuleError>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: Parameters): Result<Success, BusinessRuleError> {
        return try {
            withContext(dispatcher) {
                execute(parameters)
            }
        } catch (e: Throwable) {
//            Log.e("UseCase", "An error occurred while executing the use case", e)
            Result.Error(e.mapToAppError())
        }
    }

    protected abstract suspend fun execute(parameters: Parameters): Result<Success, BusinessRuleError>
}

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>()
    data class Error(val error: AppError) : Result<Nothing, Nothing>()
    data class BusinessRuleError<out E>(val error: E) : Result<Nothing, E>()
    data object Loading : Result<Nothing, Nothing>()

    fun isSuccessful() = this is Success
    fun hasFailed() = this is Error || this is BusinessRuleError<*>
    fun isLoading() = this is Loading

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
            is BusinessRuleError<*> -> "BusinessRuleError[error=$error]"
            Loading -> "Loading"
        }
    }
}