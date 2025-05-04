package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import ru.bjcreslin.naidizakupku.gigachat.dto.AccessTokenResponse
import ru.bjcreslin.naidizakupku.gigachat.exception.AccessTokenNotTakenException
import java.net.URI
import java.util.*


@Service
class GigachatTokenService(
    private val restTemplate: RestTemplate,
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

        val requestEntity = HttpEntity<MultiValueMap<String, String>>(formData, headers)

        val responseEntity = restTemplate.postForEntity(
            URI.create(authUrl),
            requestEntity,
            AccessTokenResponse::class.java
        )

        if (responseEntity.statusCode.is2xxSuccessful) {
            return responseEntity.body?.access_token
                ?: throw AccessTokenNotTakenException("Пустой access token")
        } else {
            throw AccessTokenNotTakenException("Не удалось получить токен: ${responseEntity.statusCode}")
        }
    }

    /***
     *  Параметр для журналирования входящих вызовов и разбора инцидентов.
     */
    private fun createRqUID(): String {
        return UUID.randomUUID().toString()
    }
}