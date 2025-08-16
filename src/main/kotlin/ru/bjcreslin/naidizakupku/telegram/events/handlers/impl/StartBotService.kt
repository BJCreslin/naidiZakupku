package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service("root#start")
class StartBotService(
    private val telegramUserService: TelegramUserService
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        
        val webAppUrl = buildWebAppUrl(chatId)
        
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
            
            🌐 Веб-приложение: $webAppUrl
            
            Если у вас есть вопросы или предложения, пожалуйста, пишите нам. Мы всегда рады помочь!
            Ваша успешная работа с госзакупками начинается здесь! 🚀
        """.trimIndent()
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
    
    private fun buildWebAppUrl(userId: Long): String {
        val baseUrl = System.getenv("WEBAPP_BASE_URL") ?: "https://naidizakupku.ru"
        val encodedUserId = URLEncoder.encode(userId.toString(), StandardCharsets.UTF_8)
        return "$baseUrl/auth?tg_user_id=$encodedUserId"
    }
}