package com.example.demo.data.repo

import com.example.demo.domain.data.CorrectionResultDomain
import com.example.demo.data.JsonExtract
import com.example.demo.PromptBuilder
import com.example.demo.data.model.CorrectionResultData
import com.example.demo.presentation.AiConfigStore
import com.example.demo.data.model.DataResult
import com.example.demo.data.network.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/**
 * the request to httpclient and httpclient itself should be put in something like HttpSource
 */
class OpenAiCompatGateway(
    private val client: HttpClient,
    private val configStore: AiConfigStore,
) : AiGateway {

    private val json: Json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun checkEnglish(
        contextText: String,
        draftText: String,
        mode: CheckMode
    ): DataResult<CorrectionResultData, CEEnglishDataError> {
        print("contextText: $contextText, draftText: $draftText, mode: $mode")

        val cfg = configStore.config.value
        val url = cfg.baseUrl.trimEnd('/') + "/chat/completions"

        val reqBody = ChatCompletionsRequest(
            model = cfg.model,
            temperature = 0.4,
            messages = listOf(
                Message(role = "system", content = PromptBuilder.buildSystemPrompt(mode).also {
                    print("system message: $it")
                }),
                Message(role = "user", content = PromptBuilder.buildUserContent(contextText, draftText).also {
                    print("user content: $it")
                })
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

            return DataResult.Success(JsonExtract.parseCorrectionResult(content))

        } catch (e: ResponseException) {
            // 例如 401/500，把服务端返回体也带上，方便你排查
            val raw = runCatching { e.response.body<String>() }.getOrNull()

            val code = e.response.status.value
            val des = e.response.status.description
            val resBody = raw ?: e.message ?: "An error occurred"

            return DataResult.Error(CEEnglishDataError.NetworkException(code, des, resBody))

        } catch (e: Exception) {
            return DataResult.Error(CEEnglishDataError.CECommonException(e))
        }
    }
}

sealed class CEEnglishDataError {
    data class NetworkException(val statusCode: Int, val description: String, val resBody: String) : CEEnglishDataError()
    data class CECommonException(val e: Exception) : CEEnglishDataError()
}
