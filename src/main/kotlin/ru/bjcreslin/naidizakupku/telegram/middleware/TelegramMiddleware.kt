package ru.bjcreslin.naidizakupku.telegram.middleware

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class TelegramMiddleware {
    
    private val logger = LoggerFactory.getLogger(TelegramMiddleware::class.java)
    
    fun logRequest(update: Update, startTime: LocalDateTime) {
        val chatId = update.message?.chatId ?: update.callbackQuery?.message?.chatId
        val username = update.message?.from?.userName ?: update.callbackQuery?.from?.userName ?: "unknown"
        val messageType = when {
            update.hasMessage() -> "MESSAGE"
            update.hasCallbackQuery() -> "CALLBACK"
            else -> "UNKNOWN"
        }
        
        logger.info("Telegram request: type=$messageType, user=$username, chatId=$chatId")
    }
    
    fun logResponse(update: Update, response: String?, startTime: LocalDateTime) {
        val duration = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
        val chatId = update.message?.chatId ?: update.callbackQuery?.message?.chatId
        val username = update.message?.from?.userName ?: update.callbackQuery?.from?.userName ?: "unknown"
        
        logger.info("Telegram response: user=$username, chatId=$chatId, duration=${duration}ms, responseLength=${response?.length ?: 0}")
    }
    
    fun logError(update: Update, error: Exception, startTime: LocalDateTime) {
        val duration = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
        val chatId = update.message?.chatId ?: update.callbackQuery?.message?.chatId
        val username = update.message?.from?.userName ?: update.callbackQuery?.from?.userName ?: "unknown"
        
        logger.error("Telegram error: user=$username, chatId=$chatId, duration=${duration}ms, error=${error.message}", error)
    }
}
