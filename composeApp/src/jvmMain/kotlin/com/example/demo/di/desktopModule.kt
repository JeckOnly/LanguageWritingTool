package com.example.demo.di

import com.example.demo.*
import com.example.demo.data.AiConfig
import com.example.demo.data.AiConfigStore
import com.example.demo.network.buildDesktopHttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

val desktopModule = module {
    single { AiConfigStore(AiConfig(baseUrl = "http://localhost:1234/v1", model = "qwen2.5-vl-3b-instruct")) }
    single { buildDesktopHttpClient(get()) }  // HttpClient
    factory { MainViewModel(get(), get()) }
    // 你还可以在这 bind UseCase、ViewModel 等
}

fun initKoin() = startKoin {
    modules(sharedModule, desktopModule)
}
