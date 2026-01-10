package com.example.demo

sealed class AppError(val message: String) {
    data class NullPointerAppError(val throwable: Throwable) : AppError(message = "空指针了")
    data class UnknownError(val throwable: Throwable) : AppError(message = throwable.message ?: "Unknown error.")
}

fun Throwable.mapToAppError(): AppError = when (this) {
    is NullPointerException -> AppError.NullPointerAppError(this)
    else -> AppError.UnknownError(this)
}