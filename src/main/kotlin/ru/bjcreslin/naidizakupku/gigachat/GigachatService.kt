package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.bjcreslin.naidizakupku.gigachat.dto.ModelList
import ru.bjcreslin.naidizakupku.gigachat.exception.GigaChatResponseException

@Service
class GigachatService(
    val restTemplate: RestTemplate,
    val gigachatTokenService: GigachatTokenService,
    @Value("\${gigachat.api.url}") private val apiUrl: String
) {

    fun getModels(): ModelList {
        val token = gigachatTokenService.getAccessToken()

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $token")
            accept = listOf(MediaType.APPLICATION_JSON)
        }

        val entity = HttpEntity<Void>(headers)

        val response = restTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            entity,
            ModelList::class.java
        )

        return response.body
            ?: throw GigaChatResponseException("Не удалось получить данные моделей")
    }
}