package ru.bjcreslin.naidizakupku.gigachat

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import ru.bjcreslin.naidizakupku.gigachat.dto.AccessTokenResponse
import ru.bjcreslin.naidizakupku.gigachat.exception.AccessTokenNotTakenException
import java.util.*

@Service
class GigachatTokenService(
    private val webClient: WebClient,
    @Value("\${gigachat.auth.url}") private val authUrl: String,
    @Value("\${gigachat.auth.client-id}") private val clientId: String,
    @Value("\${gigachat.auth.client-secret}") private val clientSecret: String,
) {

    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var tokenExpiryTime: Long = 100

    private val lock = Any()

    fun getAccessToken(): String {
        val now = System.currentTimeMillis()
        if (cachedToken != null && now < tokenExpiryTime) {
            return cachedToken!!
        }

        synchronized(lock) {
            if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
                return cachedToken!!
            }

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                accept = listOf(MediaType.APPLICATION_JSON)

                val authString = "$clientId:$clientSecret"
                val encodedAuth = Base64.getEncoder().encodeToString(authString.toByteArray())
                set("Authorization", "Basic $encodedAuth")
                set("RqUID", createRqUID())
            }

            val formData = LinkedMultiValueMap<String, String>().apply {
                add("scope", ScopeType.GIGACHAT_API_PERS.toString())
            }

            val rawResponse = webClient.post()
                .uri(authUrl)
                .headers { it.addAll(headers) }
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String::class.java)
                .doOnNext { println("Raw response JSON: $it") }
                .block() ?: throw AccessTokenNotTakenException("Пустой ответ от сервера")

            val response = try {
                jacksonObjectMapper().readValue(rawResponse, AccessTokenResponse::class.java)
            } catch (e: Exception) {
                throw AccessTokenNotTakenException("Ошибка парсинга ответа: ${e.message}")
            }

            tokenExpiryTime = response.expiresAt
            cachedToken = response.accessToken
            return cachedToken!!
        }
    }

    /***
     *  Параметр для журналирования входящих вызовов и разбора инцидентов.
     */
    private fun createRqUID(): String {
        return UUID.randomUUID().toString()
    }
}