package ru.bjcreslin.naidizakupku.gigachat

import chat.giga.GigaChat
import chat.giga.GigaChatBuilder
import chat.giga.models.ChatMessage
import chat.giga.models.ChatResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService

@Service
class GigachatService(
    @Value("\${gigachat.auth.client-id}")
    private val clientId: String,
    @Value("\${gigachat.auth.client-secret}")
    private val clientSecret: String,
    @Value("\${gigachat.auth.url}")
    private val authUrl: String,
    @Value("\${gigachat.api.url}")
    private val apiUrl: String,
    private val customMetricsService: CustomMetricsService
) {

    private val logger = LoggerFactory.getLogger(GigachatService::class.java)
    private val gigaChat: GigaChat = GigaChatBuilder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .authUrl(authUrl)
        .apiUrl(apiUrl)
        .build()

    fun sendMessage(messages: List<ChatMessage>, model: String = "GigaChat:latest"): ChatResponse {
        val startTime = System.currentTimeMillis()
        
        try {
            val response = gigaChat.chat(messages, model)
            
            val processingTime = System.currentTimeMillis() - startTime
            customMetricsService.recordGigaChatRequestTime(model, processingTime)
            
            logger.debug("GigaChat request completed in ${processingTime}ms using model: $model")
            
            return response
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            customMetricsService.recordGigaChatRequestTime(model, processingTime)
            
            logger.error("Error in GigaChat request using model: $model", e)
            throw e
        }
    }

    fun sendMessage(message: String, model: String = "GigaChat:latest"): ChatResponse {
        val chatMessage = ChatMessage.builder()
            .role("user")
            .content(message)
            .build()
        
        return sendMessage(listOf(chatMessage), model)
    }
}