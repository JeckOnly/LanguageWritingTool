package com.example.demo.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.net.Proxy
import java.util.concurrent.TimeUnit

fun buildDesktopHttpClient(json: Json): HttpClient {
    val ok = OkHttpClient.Builder()
        .build()

    return HttpClient(OkHttp) {
        engine {
            // 把预配置 OkHttpClient 注入进去
            preconfigured = ok
        }

        install(ContentNegotiation) { json(json) }

        install(HttpTimeout) {
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 20_000
            socketTimeoutMillis = 20_000
        }
    }
}
