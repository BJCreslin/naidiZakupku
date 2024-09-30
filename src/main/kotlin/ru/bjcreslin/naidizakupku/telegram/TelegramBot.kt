package ru.bjcreslin.naidizakupku.telegram

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration
import ru.bjcreslin.naidizakupku.telegram.events.CallbackEvent
import ru.bjcreslin.naidizakupku.telegram.events.MessageEvent
import ru.bjcreslin.naidizakupku.telegram.exception.TelegramBotServiceException

@Service
class TelegramBot(
    val config: BotConfiguration,
    val callbackEvent: CallbackEvent,
    val messageEvent: MessageEvent
) : TelegramLongPollingBot(config.token) {

    val logger: Logger = LoggerFactory.getLogger(TelegramBot::class.java)

    override fun getBotUsername(): String {
        return config.name
    }

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            val response: SendMessage? = if (update.hasMessage()) {
                logger.info("received new message from ${update.message.chatId}")
                messageEvent.action(update)
            } else if (update.hasCallbackQuery()) {
                logger.info("received new callback from ${update.callbackQuery.message.chatId}")
                callbackEvent.action(update)
            } else {
                logger.warn("Unknown telegram message type received. $update")
                null
            }
            try {
                if (response != null) {
                    execute<Message, SendMessage>(response)
                }
            } catch (ex: TelegramApiException) {
                val eMessage =
                    response?.let { String.format(TelegramBotServiceException.ERROR_SEND_MESSAGE, it.chatId, it.text) }
                throw TelegramBotServiceException(eMessage, ex)
            }
        }
    }

    override fun getBotToken(): String {
        return super.getBotToken()
    }


}