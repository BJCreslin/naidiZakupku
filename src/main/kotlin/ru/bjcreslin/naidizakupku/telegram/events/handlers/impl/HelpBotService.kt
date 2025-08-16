package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("root#help")
class HelpBotService(
    private val telegramUserService: TelegramUserService
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        return buildHelpMessage()
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }

    private fun buildHelpMessage(): String {
        return """
            🤖 *Справка по командам бота*

            📋 *Основные команды:*
            /start - Начать работу с ботом
            /help - Показать эту справку
            /code - Получить код для браузерного расширения
            /list - Показать список сохраненных закупок

            🤖 *AI-функции:*
            /gigachat - Перейти в режим работы с GigaChat
            /model - Настройки модели AI
            /prompt - Управление промптами
            /reset - Сбросить контекст AI

            📊 *Статистика:*
            /stats - Показать статистику по закупкам
            /users - Список пользователей (для админов)

            💡 *Как использовать:*
            1. Установите браузерное расширение
            2. Получите код командой /code
            3. Добавляйте интересные закупки через расширение
            4. Просматривайте список командой /list
            5. Используйте AI для анализа закупок

            ❓ *Нужна помощь?* Обратитесь к администратору.
        """.trimIndent()
    }
}