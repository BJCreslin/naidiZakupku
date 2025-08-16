@file:Suppress("DEPRECATION")
package ru.bjcreslin.naidizakupku.telegram

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration
import ru.bjcreslin.naidizakupku.telegram.events.CallbackEvent
import ru.bjcreslin.naidizakupku.telegram.events.MessageEvent
import ru.bjcreslin.naidizakupku.telegram.exception.TelegramBotServiceException
import ru.bjcreslin.naidizakupku.telegram.middleware.TelegramMiddleware
import ru.bjcreslin.naidizakupku.telegram.service.TelegramUpdateDeduplicationService
import java.time.LocalDateTime

@Service
class TelegramBot(
    private val config: BotConfiguration,
    private val callbackEvent: CallbackEvent,
    private val messageEvent: MessageEvent,
    private val deduplicationService: TelegramUpdateDeduplicationService,
    private val middleware: TelegramMiddleware
) : TelegramLongPollingBot(config.token) {

    private val logger: Logger = LoggerFactory.getLogger(TelegramBot::class.java)

    override fun getBotUsername(): String {
        return config.name
    }

    override fun onUpdateReceived(update: Update) {
        val startTime = LocalDateTime.now()
        val updateId = update.updateId
        
        try {
            // Логируем входящий запрос
            middleware.logRequest(update, startTime)
            
            // Проверяем через Spring Cache, не обрабатывали ли мы уже этот update
            if (deduplicationService.isUpdateProcessed(updateId)) {
                logger.warn("Duplicate update received with id: $updateId, skipping")
                return
            }
            
            // Отмечаем update как обработанный через Spring Cache
            deduplicationService.markAsProcessed(updateId)
            
            val response: SendMessage? = buildResponse(update)
            
            if (response != null) {
                execute(response)
                middleware.logResponse(update, response.text, startTime)
            }
            
        } catch (ex: TelegramApiException) {
            middleware.logError(update, ex, startTime)
            val eMessage = String.format(
                TelegramBotServiceException.ERROR_SEND_MESSAGE,
                update.message?.chatId ?: update.callbackQuery?.message?.chatId,
                "Failed to send response"
            )
            throw TelegramBotServiceException(eMessage, ex)
        } catch (ex: Exception) {
            middleware.logError(update, ex, startTime)
            logger.error("Unexpected error processing update: ${ex.message}", ex)
            
            // Отправляем сообщение об ошибке пользователю
            try {
                val errorMessage = SendMessage().apply {
                    chatId = (update.message?.chatId ?: update.callbackQuery?.message?.chatId).toString()
                    text = "Произошла ошибка при обработке запроса. Попробуйте позже или обратитесь к администратору."
                    enableMarkdown(true)
                }
                execute(errorMessage)
            } catch (sendError: Exception) {
                logger.error("Failed to send error message: ${sendError.message}", sendError)
            }
        }
    }

    private fun buildResponse(update: Update): SendMessage? {
        return when {
            update.hasMessage() -> {
                val chatId = update.message.chatId
                logger.info("Processing message from chatId: $chatId")
                messageEvent.action(update)
            }
            update.hasCallbackQuery() -> {
                val chatId = update.callbackQuery.message.chatId
                logger.info("Processing callback from chatId: $chatId")
                callbackEvent.action(update)
            }
            else -> {
                logger.warn("Unknown telegram message type received: ${update.javaClass.simpleName}")
                null
            }
        }
    }
}