package com.example.demo.data.network.model

import kotlinx.serialization.Serializable

// TODO: should be put in another module to limit visibility
@Serializable
 data class ChatCompletionsRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double? = null
)

@Serializable
 data class Message(
    val role: String,
    val content: String
)

@Serializable
 data class ChatCompletionsResponse(
    val choices: List<Choice> = emptyList()
)

@Serializable
 data class Choice(
    val message: MessageOut? = null
)

@Serializable
 data class MessageOut(
    val content: String? = null
)