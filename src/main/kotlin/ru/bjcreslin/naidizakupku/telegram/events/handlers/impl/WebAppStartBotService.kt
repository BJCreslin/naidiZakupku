package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class WebAppStartBotService(
    private val botConfiguration: BotConfiguration
) : CommandHandler {
    
    private val logger = LoggerFactory.getLogger(WebAppStartBotService::class.java)
    
    override fun canHandle(update: Update): Boolean {
        return update.hasMessage() && 
               update.message.hasText() && 
               update.message.text == "/start"
    }
    
    override fun handle(update: Update): SendMessage {
        val chatId = update.message.chatId.toString()
        val user = update.message.from
        
        logger.info("Handling /start command from user: ${user.id}")
        
        val message = SendMessage()
        message.chatId = chatId
        message.text = """
            👋 Привет, ${user.firstName}!
            
            Добро пожаловать в систему "Найди Закупку" 🏢
            
            Здесь вы можете:
            • 🔍 Искать закупки по различным критериям
            • 📊 Получать статистику по закупкам
            • 🔔 Настраивать уведомления
            • 💬 Задавать вопросы через AI-ассистента
            
            Нажмите кнопку ниже, чтобы открыть веб-приложение:
        """.trimIndent()
        
        // Создаем Web App кнопку
        val webAppUrl = buildWebAppUrl(user.id)
        val webAppButton = InlineKeyboardButton()
        webAppButton.text = "🌐 Открыть приложение"
        webAppButton.webApp = org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo(webAppUrl)
        
        // Создаем клавиатуру
        val keyboard = InlineKeyboardMarkup()
        keyboard.keyboard = listOf(listOf(webAppButton))
        message.replyMarkup = keyboard
        
        return message
    }
    
    private fun buildWebAppUrl(userId: Long): String {
        val baseUrl = System.getenv("WEBAPP_BASE_URL") ?: "https://naidizakupku.ru"
        val encodedUserId = URLEncoder.encode(userId.toString(), StandardCharsets.UTF_8)
        return "$baseUrl/auth?tg_user_id=$encodedUserId"
    }
}
