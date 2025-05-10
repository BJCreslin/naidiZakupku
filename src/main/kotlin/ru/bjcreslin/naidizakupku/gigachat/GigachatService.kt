package ru.bjcreslin.naidizakupku.gigachat

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.bjcreslin.naidizakupku.gigachat.dto.ModelList
import ru.bjcreslin.naidizakupku.gigachat.exception.GigaChatResponseException
import org.slf4j.Logger

@Service
class GigachatService(
    val gigachatTokenService: GigachatTokenService,
    private val unsafeWebClient: WebClient,
    @Value("\${gigachat.api.url}") private val apiUrl: String
) {
    private val logger: Logger = LoggerFactory.getLogger(GigachatService::class.java)

    fun getModels(): ModelList {
        val token = gigachatTokenService.getAccessToken()
        logger.info("Отправлен запрос на получение данных моделей: {$token}")

        val response = unsafeWebClient.get()
            .uri("$apiUrl/models")
            .headers {
                it.setBearerAuth(token)
                it["accept"] = MediaType.APPLICATION_JSON_VALUE
            }
            .retrieve()
            .bodyToMono(ModelList::class.java)
            .block()

        return response
            ?: throw GigaChatResponseException("Не удалось получить данные моделей")
    }
}