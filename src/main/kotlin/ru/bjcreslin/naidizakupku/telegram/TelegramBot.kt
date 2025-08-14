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
import ru.bjcreslin.naidizakupku.telegram.service.TelegramUpdateDeduplicationService

@Service
class TelegramBot(
    private val config: BotConfiguration,
    private val callbackEvent: CallbackEvent,
    private val messageEvent: MessageEvent,
    private val deduplicationService: TelegramUpdateDeduplicationService
) : TelegramLongPollingBot(config.token) {

    private val logger: Logger = LoggerFactory.getLogger(TelegramBot::class.java)

    override fun getBotUsername(): String {
        return config.name
    }

    override fun onUpdateReceived(update: Update) {
        val updateId = update.updateId
        
        // Проверяем через Spring Cache, не обрабатывали ли мы уже этот update
        if (deduplicationService.isUpdateProcessed(updateId)) {
            logger.warn("Duplicate update received with id: $updateId, skipping")
            return
        }
        
        // Отмечаем update как обработанный через Spring Cache
        deduplicationService.markAsProcessed(updateId)
        
        val response: SendMessage? = buildResponse(update)
        try {
            if (response != null) execute(response)
        } catch (ex: TelegramApiException) {
            val eMessage = response?.let {
                String.format(
                    TelegramBotServiceException.ERROR_SEND_MESSAGE,
                    it.chatId,
                    it.text
                )
            }
            throw TelegramBotServiceException(eMessage, ex)
        }
    }

    private fun buildResponse(update: Update): SendMessage? {
        return when {
            update.hasMessage() -> {
                logger.info("received new message from ${update.message.chatId}")
                messageEvent.action(update)
            }
            update.hasCallbackQuery() -> {
				val chatLabel: String = (update.callbackQuery.message?.chatId ?: update.callbackQuery.from?.id)
					?.toString() ?: "unknown"
				logger.info("received new callback from $chatLabel")
                callbackEvent.action(update)
            }
            else -> {
                logger.warn("Unknown telegram message type received. $update")
                null
            }
        }
    }
}