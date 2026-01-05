package com.example.demo.di

import com.example.demo.AiGateway
import com.example.demo.data.AiConfig
import com.example.demo.data.AiConfigStore
import com.example.demo.domain.CheckEnglishUseCase
import com.example.demo.network.OpenAiCompatGateway
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule: Module = module {
    single { AiConfigStore(AiConfig(baseUrl = "http://localhost:1234/v1", model = "qwen2.5-vl-3b-instruct")) }
    single {
        Json { ignoreUnknownKeys = true; isLenient = true }
    }

    factory { CheckEnglishUseCase(get()) }
    // HttpClient 由平台侧提供（desktop/ios/android）
    factory<AiGateway> { OpenAiCompatGateway(get(), get()) }

}
