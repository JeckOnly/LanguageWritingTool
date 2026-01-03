package com.example.demo.network

import com.example.demo.AiGateway
import com.example.demo.CheckMode
import com.example.demo.CorrectionResult
import com.example.demo.JsonExtract
import com.example.demo.PromptBuilder
import com.example.demo.data.AiConfigStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OpenAiCompatGateway(
    private val client: HttpClient,
    private val configStore: AiConfigStore,
) : AiGateway {

    private val json: Json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun checkEnglish(
        contextText: String,
        draftText: String,
        mode: CheckMode
    ): CorrectionResult {

        val cfg = configStore.config.value
        val url = cfg.baseUrl.trimEnd('/') + "/chat/completions"

        val reqBody = ChatCompletionsRequest(
            model = cfg.model,
            temperature = 0.2,
            messages = listOf(
                Message(role = "system", content = PromptBuilder.buildSystemPrompt(mode)),
                Message(role = "user", content = PromptBuilder.buildUserContent(contextText, draftText))
            )
        )

        try {
            val resp = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(reqBody)
                val key = cfg.apiKey?.trim()
                if (!key.isNullOrEmpty()) {
                    header("Authorization", "Bearer $key")
                }
            }.body<ChatCompletionsResponse>()

            val content = resp.choices.firstOrNull()?.message?.content
                ?: error("No choices/message.content in response")

            return JsonExtract.parseCorrectionResult(content)

        } catch (e: ResponseException) {
            // 例如 401/500，把服务端返回体也带上，方便你排查
            val raw = runCatching { e.response.body<String>() }.getOrNull()
            error("HTTP ${e.response.status.value}: ${raw ?: e.message}")
        }
    }
}

@Serializable
private data class ChatCompletionsRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double? = null
)

@Serializable
private data class Message(
    val role: String,
    val content: String
)

@Serializable
private data class ChatCompletionsResponse(
    val choices: List<Choice> = emptyList()
)

@Serializable
private data class Choice(
    val message: MessageOut? = null
)

@Serializable
private data class MessageOut(
    val content: String? = null
)
