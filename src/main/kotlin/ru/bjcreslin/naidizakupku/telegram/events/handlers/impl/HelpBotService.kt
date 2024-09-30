package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("/help")
class HelpBotService(
    private val telegramUserService: TelegramUserService
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        return """
            Здравствуйте! Добро пожаловать в бота по уведомлениям для поставщиков Госзакупок.
            Этот бот поможет вам быть в курсе актуальных тендеров и государственных закупок. Вот что вы можете сделать с помощью наших команд:
            /code - Получить код для браузерного расширения.
            /help - Получить дополнительную помощь и инструкции.
            Если у вас есть вопросы или предложения, пожалуйста, пишите нам. Мы всегда рады помочь!
            Ваша успешная работа с госзакупками начинается здесь!
        """.trimIndent()
    }
}