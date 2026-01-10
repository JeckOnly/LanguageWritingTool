package com.example.demo.di

import com.example.demo.network.buildDesktopHttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

val desktopModule = module {

    single { buildDesktopHttpClient(get()) }  // HttpClient
    // 你还可以在这 bind UseCase、ViewModel 等
}

fun initKoin() = startKoin {
    modules(sharedModule, desktopModule)
}
