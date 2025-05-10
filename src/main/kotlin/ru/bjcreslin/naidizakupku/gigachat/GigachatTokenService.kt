package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
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
        }

        val headers = HttpHeaders().apply {
            contentType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
            accept = listOf(org.springframework.http.MediaType.APPLICATION_JSON)

            val authString = "$clientId:$clientSecret"
            val encodedAuth = Base64.getEncoder().encodeToString(authString.toByteArray())
            set("Authorization", "Basic $encodedAuth")

            set("RqUID", createRqUID())
        }

        val formData = LinkedMultiValueMap<String, String>().apply {
            add("scope", ScopeType.GIGACHAT_API_PERS.toString())
        }

        return webClient.post()
            .uri(authUrl)
            .headers { it.addAll(headers) }
            .bodyValue(formData)
            .retrieve()
            .bodyToMono(AccessTokenResponse::class.java)
            .flatMap { response ->
                if (response.access_token != null) {
                    Mono.just(response.access_token)
                } else {
                    Mono.error(AccessTokenNotTakenException("Пустой access token"))
                }
            }
            .doOnTerminate { tokenExpiryTime = System.currentTimeMillis() + 3600000 }
            .block() ?: throw AccessTokenNotTakenException("Ошибка получения access token")
    }

    /***
     *  Параметр для журналирования входящих вызовов и разбора инцидентов.
     */
    private fun createRqUID(): String {
        return UUID.randomUUID().toString()
    }
}