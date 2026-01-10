package com.example.demo.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AiConfig(
    val baseUrl: String,   // e.g. http://localhost:1234/v1
    val model: String,     // e.g. llama3.2
    val apiKey: String? = null
)

// TODO: should save in database
class AiConfigStore(initial: AiConfig) {
    private val _config = MutableStateFlow(initial)
    val config: StateFlow<AiConfig> = _config

    fun update(transform: (AiConfig) -> AiConfig) {
        _config.value = transform(_config.value)
    }
}
