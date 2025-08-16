package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("root#start")
class StartBotService(
    private val telegramUserService: TelegramUserService
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        return """
            👋 Здравствуйте! Добро пожаловать в бота "Найди Закупку"!
            
            🏢 Этот бот поможет вам быть в курсе актуальных тендеров и государственных закупок.
            
            📱 Доступные команды:
            • /start - Открыть веб-приложение
            • /code - Получить код для браузерного расширения
            • /help - Получить дополнительную помощь и инструкции
            • /procurements - Список закупок
            • /stats - Статистика
            • /gigachat - AI-ассистент
            
            🌐 Нажмите кнопку "Открыть приложение" для доступа к полному функционалу!
            
            Если у вас есть вопросы или предложения, пожалуйста, пишите нам. Мы всегда рады помочь!
            Ваша успешная работа с госзакупками начинается здесь! 🚀
        """.trimIndent()
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
}