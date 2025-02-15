package ru.bjcreslin.naidizakupku.telegram.init

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.meta.generics.TelegramBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration

@Component
class BotInitializer(
    private val botConfiguration: BotConfiguration,
    private val telegramBot: TelegramBot
) {
    val logger: Logger = LoggerFactory.getLogger(BotInitializer::class.java)

    @EventListener(ContextRefreshedEvent::class)
    @Throws(TelegramApiException::class)
    fun init() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        try {
            telegramBotsApi.registerBot(telegramBot as LongPollingBot)
            logger.info("Registered telegram Bot")
        } catch (e: TelegramApiRequestException) {
            logger.error(e.message)
        }
    }
}
