package ru.bjcreslin.naidizakupku.cfg

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GigaChatConfiguration {
    @Value("\${gigachat.auth.url}")
    lateinit var authUrl: String

    @Value("\${gigachat.api.url}")
    lateinit var apiUrl: String

    @Value("\${gigachat.auth.client-id}")
    lateinit var clientId: String

    @Value("\${gigachat.auth.client-secret}")
    lateinit var clientSecret: String
}