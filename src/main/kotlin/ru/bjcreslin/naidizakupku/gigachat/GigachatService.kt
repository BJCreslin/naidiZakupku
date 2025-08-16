package ru.bjcreslin.naidizakupku.gigachat

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService

@Service
class GigachatService(
    @Value("\${gigachat.auth.client-id:default-client-id}")
    private val clientId: String,
    @Value("\${gigachat.auth.client-secret:default-client-secret}")
    private val clientSecret: String,
    @Value("\${gigachat.auth.url:https://ngw.devices.sberbank.ru:9443/api/v2/oauth}")
    private val authUrl: String,
    @Value("\${gigachat.api.url:https://gigachat.devices.sberbank.ru/api/v1}")
    private val apiUrl: String,
    private val customMetricsService: CustomMetricsService
) {

    private val logger = LoggerFactory.getLogger(GigachatService::class.java)

    fun sendMessage(messages: List<ChatMessage>, model: String = "GigaChat:latest"): ChatResponse {
        val startTime = System.currentTimeMillis()
        
        try {
            // Заглушка - возвращаем тестовый ответ
            val response = ChatResponse(
                choices = listOf(
                    ChatChoice(
                        message = ChatMessage(
                            role = "assistant",
                            content = "Это тестовый ответ от GigaChat. Реальная интеграция в разработке."
                        )
                    )
                )
            )
            
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
        val chatMessage = ChatMessage(
            role = "user",
            content = message
        )
        
        return sendMessage(listOf(chatMessage), model)
    }

    fun getModels(): List<String> {
        return listOf("GigaChat:latest", "GigaChat:pro", "GigaChat:base")
    }
}

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: ChatMessage
)