package ru.bjcreslin.naidizakupku.telegram.events.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.bjcreslin.naidizakupku.telegram.events.CallbackEvent
import ru.bjcreslin.naidizakupku.telegram.events.handlers.impl.ProcurementsListBotService

@Service
class CallbackEventImpl(
    private val procurementsListService: ProcurementsListBotService
) : CallbackEvent {
    
    private val logger = LoggerFactory.getLogger(CallbackEventImpl::class.java)

    override fun action(update: Update): SendMessage {
        val callbackQuery = update.callbackQuery
        val chatId = callbackQuery.message.chatId
        val callbackData = callbackQuery.data
        val username = callbackQuery.from.userName ?: "unknown"

        logger.info("Processing callback from user $username (chatId: $chatId): $callbackData")

        try {
            return when {
                callbackData.startsWith("procurement_") -> {
                    handleProcurementCallback(callbackData, chatId)
                }
                callbackData.startsWith("filter_") -> {
                    handleFilterCallback(callbackData, chatId)
                }
                callbackData.startsWith("page_") -> {
                    handlePaginationCallback(callbackData, chatId)
                }
                callbackData == "refresh" -> {
                    handleRefreshCallback(chatId)
                }
                callbackData == "back_to_list" -> {
                    handleBackToListCallback(chatId)
                }
                callbackData.startsWith("delete_procurement_") -> {
                    handleDeleteProcurementCallback(callbackData, chatId)
                }
                else -> {
                    val message = SendMessage()
                    message.chatId = chatId.toString()
                    message.text = "Неизвестный callback: $callbackData"
                    message.enableMarkdown(true)
                    logger.warn("Unknown callback data: $callbackData from user $username")
                    message
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing callback from user $username: ${e.message}", e)
            val message = SendMessage()
            message.chatId = chatId.toString()
            message.text = "Произошла ошибка при обработке запроса. Попробуйте позже."
            message.enableMarkdown(true)
            return message
        }
    }

    private fun handleProcurementCallback(callbackData: String, chatId: Long): SendMessage {
        val procurementId = callbackData.removePrefix("procurement_")
        
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = formatProcurementDetails(procurementId.toLongOrNull())
        message.enableMarkdown(true)
        message.replyMarkup = createProcurementDetailKeyboard(procurementId.toLongOrNull())
        
        return message
    }
    
    private fun formatProcurementDetails(procurementId: Long?): String {
        if (procurementId == null) {
            return "❌ Ошибка: неверный ID закупки"
        }
        
        // TODO: Получить закупку из базы данных
        return buildString {
            append("📋 *Детали закупки #$procurementId*\n\n")
            append("🏢 *Название:* Закупка товаров/услуг\n")
            append("📄 *ФЗ:* 44-ФЗ\n")
            append("🔢 *Реестровый номер:* 123456789\n")
            append("💰 *Цена:* 1 000 000 ₽\n")
            append("🏛️ *Заказчик:* Министерство\n")
            append("📅 *Дата размещения:* 01.01.2024\n")
            append("🔗 *Ссылка:* [Открыть закупку](https://zakupki.gov.ru)\n\n")
            append("_Функция в разработке..._")
        }
    }
    
    private fun createProcurementDetailKeyboard(procurementId: Long?): InlineKeyboardMarkup? {
        if (procurementId == null) return null
        
        val keyboard = mutableListOf<MutableList<InlineKeyboardButton>>()
        
        // Кнопка "Назад к списку"
        val backRow = mutableListOf<InlineKeyboardButton>()
        val backButton = InlineKeyboardButton()
        backButton.text = "⬅️ Назад к списку"
        backButton.callbackData = "back_to_list"
        backRow.add(backButton)
        keyboard.add(backRow)
        
        // Кнопка "Удалить из списка"
        val deleteRow = mutableListOf<InlineKeyboardButton>()
        val deleteButton = InlineKeyboardButton()
        deleteButton.text = "🗑️ Удалить из списка"
        deleteButton.callbackData = "delete_procurement_$procurementId"
        deleteRow.add(deleteButton)
        keyboard.add(deleteRow)
        
        val markup = InlineKeyboardMarkup()
        markup.keyboard = keyboard
        return markup
    }

    private fun handleFilterCallback(callbackData: String, chatId: Long): SendMessage {
        val filterType = callbackData.removePrefix("filter_")
        val filterText = when (filterType) {
            "price" -> "по цене"
            "date" -> "по дате"
            else -> filterType
        }
        
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "🔍 *Применен фильтр: $filterText*\n\nФункция сортировки в разработке..."
        message.enableMarkdown(true)
        
        return message
    }

    private fun handlePaginationCallback(callbackData: String, chatId: Long): SendMessage {
        val page = callbackData.removePrefix("page_").toIntOrNull() ?: 0
        return procurementsListService.executeWithPagination(chatId, page)
    }

    private fun handleRefreshCallback(chatId: Long): SendMessage {
        return procurementsListService.executeWithPagination(chatId, 0)
    }
    
    private fun handleBackToListCallback(chatId: Long): SendMessage {
        return procurementsListService.executeWithPagination(chatId, 0)
    }
    
    private fun handleDeleteProcurementCallback(callbackData: String, chatId: Long): SendMessage {
        val procurementId = callbackData.removePrefix("delete_procurement_")
        
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "🗑️ *Удаление закупки #$procurementId*\n\nФункция удаления в разработке..."
        message.enableMarkdown(true)
        
        val keyboard = mutableListOf<MutableList<InlineKeyboardButton>>()
        val backRow = mutableListOf<InlineKeyboardButton>()
        val backButton = InlineKeyboardButton()
        backButton.text = "⬅️ Назад к списку"
        backButton.callbackData = "back_to_list"
        backRow.add(backButton)
        keyboard.add(backRow)
        
        val markup = InlineKeyboardMarkup()
        markup.keyboard = keyboard
        message.replyMarkup = markup
        
        return message
    }
}