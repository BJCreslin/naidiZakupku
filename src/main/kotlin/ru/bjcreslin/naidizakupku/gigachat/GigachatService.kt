package ru.bjcreslin.naidizakupku.gigachat

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.bjcreslin.naidizakupku.gigachat.dto.GigaChatModelList
import ru.bjcreslin.naidizakupku.gigachat.exception.GigaChatResponseException
import org.slf4j.Logger
import ru.bjcreslin.naidizakupku.gigachat.dto.GigaChatRequest
import ru.bjcreslin.naidizakupku.gigachat.dto.GigaChatResponse

@Service
class GigachatService(
    val gigachatTokenService: GigachatTokenService,
    private val unsafeWebClient: WebClient,
    @Value("\${gigachat.api.url}") private val apiUrl: String
) {
    private val logger: Logger = LoggerFactory.getLogger(GigachatService::class.java)


    fun getModels(): GigaChatModelList {
        val token = gigachatTokenService.getAccessToken()
        logger.info("Отправлен запрос на получение данных моделей: {$token}")

        val response = unsafeWebClient.get()
            .uri("$apiUrl/models")
            .headers {
                it.setBearerAuth(token)
                it["accept"] = MediaType.APPLICATION_JSON_VALUE
            }
            .retrieve()
            .bodyToMono(GigaChatModelList::class.java)
            .block()

        return response
            ?: throw GigaChatResponseException("Не удалось получить данные моделей")
    }

    fun getAnswer(prompt: String): String {
        val token = gigachatTokenService.getAccessToken()
        logger.info("Отправлен запрос на получение ответа")

        val requestBody = createRequestBody(prompt)

        val response = unsafeWebClient.post()
            .uri("$apiUrl/chat/completions")
            .headers {
                it.setBearerAuth(token)
                it["accept"] = MediaType.APPLICATION_JSON_VALUE
                it["content-type"] = MediaType.APPLICATION_JSON_VALUE
            }
            .bodyValue(requestBody)

            .retrieve()
            .bodyToMono(GigaChatResponse::class.java)
            .block()

        return response?.choices?.first()?.message?.content ?: ""
    }

    private fun createRequestBody(prompt: String) =
        mapOf(
               "model" to "GigaChat",
               "messages" to listOf(
                   mapOf(
                       "role" to "system",
                       "content" to "Ты Пушкин А.С."
                   ),
                   mapOf(
                       "role" to "user",
                       "content" to prompt
                   )
               ),
               "stream" to false,
               "update_interval" to 0
           )
    }