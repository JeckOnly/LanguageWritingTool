package com.example.demo.di

import com.example.demo.data.repo.AiGateway
import com.example.demo.presentation.AiConfig
import com.example.demo.presentation.AiConfigStore
import com.example.demo.domain.usecase.CheckEnglishUseCase
import com.example.demo.data.repo.OpenAiCompatGateway
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val DefaultDispatcher = named("DefaultDispatcher")
private val MainDispatcher = named("MainDispatcher")


val sharedModule: Module = module {
    single { AiConfigStore(AiConfig(baseUrl = "http://localhost:1234/v1", model = "qwen2.5-vl-3b-instruct")) }
    single {
        Json { ignoreUnknownKeys = true; isLenient = true }
    }
    single<CoroutineDispatcher>(DefaultDispatcher) { Dispatchers.Default }
    single<CoroutineDispatcher>(MainDispatcher) { Dispatchers.Main }

    factory { CheckEnglishUseCase(get(), get(DefaultDispatcher)) }
    // HttpClient 由平台侧提供（desktop/ios/android）
    factory<AiGateway> { OpenAiCompatGateway(get(), get()) }

}
