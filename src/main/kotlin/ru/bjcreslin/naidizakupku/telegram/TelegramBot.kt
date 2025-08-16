@file:Suppress("DEPRECATION")
package ru.bjcreslin.naidizakupku.telegram

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import ru.bjcreslin.naidizakupku.telegram.events.CallbackEvent
import ru.bjcreslin.naidizakupku.telegram.events.MessageEvent
import ru.bjcreslin.naidizakupku.telegram.middleware.TelegramMiddleware
import ru.bjcreslin.naidizakupku.telegram.service.TelegramUpdateDeduplicationService

@Component
class TelegramBot(
    private val messageEvent: MessageEvent,
    private val callbackEvent: CallbackEvent,
    private val telegramMiddleware: TelegramMiddleware,
    private val telegramUpdateDeduplicationService: TelegramUpdateDeduplicationService,
    private val customMetricsService: CustomMetricsService
) : TelegramLongPollingBot() {

    private val logger = LoggerFactory.getLogger(TelegramBot::class.java)

    override fun getBotToken(): String {
        return System.getenv("NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN") ?: ""
    }

    override fun getBotUsername(): String {
        return System.getenv("NAIDI_ZAKUPKU_TELEGRAM_BOT_NAME") ?: ""
    }

    override fun onUpdateReceived(update: Update) {
        val startTime = System.currentTimeMillis()
        val updateId = update.updateId
        
        try {
            // Проверка на дубликаты
            if (telegramUpdateDeduplicationService.isDuplicate(updateId)) {
                logger.debug("Duplicate update received: $updateId")
                customMetricsService.incrementApiRequestCounter("telegram.duplicate", 200)
                return
            }

            // Обработка через middleware
            val processedUpdate = telegramMiddleware.process(update)
            
            when {
                processedUpdate.hasCallbackQuery() -> {
                    logger.info("Processing callback query from user: ${processedUpdate.callbackQuery.from.id}")
                    callbackEvent.handle(processedUpdate)
                    customMetricsService.incrementTelegramCommandCounter("callback")
                }
                processedUpdate.hasMessage() -> {
                    logger.info("Processing message from user: ${processedUpdate.message.from.id}")
                    messageEvent.handle(processedUpdate)
                    customMetricsService.incrementTelegramCommandCounter("message")
                }
                else -> {
                    logger.warn("Unknown update type received: $updateId")
                    customMetricsService.incrementApiRequestCounter("telegram.unknown", 400)
                }
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            customMetricsService.recordTelegramUpdateProcessingTime(updateId.toLong(), processingTime)
            
            logger.debug("Update $updateId processed in ${processingTime}ms")
            
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            customMetricsService.recordTelegramUpdateProcessingTime(updateId.toLong(), processingTime)
            customMetricsService.incrementApiRequestCounter("telegram.error", 500)
            
            logger.error("Error processing update $updateId", e)
            
            // Отправка сообщения об ошибке пользователю
            try {
                val errorMessage = SendMessage()
                errorMessage.chatId = update.message?.chatId?.toString() ?: update.callbackQuery?.message?.chatId?.toString()
                errorMessage.text = "Произошла ошибка при обработке запроса. Попробуйте позже."
                execute(errorMessage)
            } catch (telegramException: TelegramApiException) {
                logger.error("Failed to send error message", telegramException)
            }
        }
    }
}