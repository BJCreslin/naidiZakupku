package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService
import java.math.BigDecimal

@Service("root#list")
class ProcurementsListBotService(
    private val telegramUserService: TelegramUserService,
    private val procurementService: ProcurementService
) : CommandHandler {

    companion object {
        private const val ITEMS_PER_PAGE = 5
    }

    override fun execute(chatId: Long, params: String): String {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val procurements = procurementService.getAllProcurementsForTelegram(user)
        return formatProcurementsAsTable(procurements)
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }

    fun executeWithPagination(chatId: Long, page: Int = 0): SendMessage {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val procurements = procurementService.getAllProcurementsForTelegram(user)
        
        val totalPages = (procurements.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
        val currentPage = page.coerceIn(0, totalPages - 1)
        
        val startIndex = currentPage * ITEMS_PER_PAGE
        val endIndex = minOf(startIndex + ITEMS_PER_PAGE, procurements.size)
        val pageProcurements = procurements.subList(startIndex, endIndex)
        
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = formatProcurementsWithPagination(procurements, page)
        message.enableMarkdown(true)
        
        // Создаем комбинированную клавиатуру с навигацией и кнопками закупок
        val combinedKeyboard = createCombinedKeyboard(procurements.size, currentPage, pageProcurements, startIndex)
        message.replyMarkup = combinedKeyboard
        
        return message
    }

    private fun formatProcurementsWithPagination(procurements: List<Procurement>, page: Int): String {
        if (procurements.isEmpty()) {
            return "📋 Нет сохраненных закупок.\n\nИспользуйте браузерное расширение для добавления закупок в список."
        }

        val totalPages = (procurements.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
        val currentPage = page.coerceIn(0, totalPages - 1)
        
        val startIndex = currentPage * ITEMS_PER_PAGE
        val endIndex = minOf(startIndex + ITEMS_PER_PAGE, procurements.size)
        val pageProcurements = procurements.subList(startIndex, endIndex)
        
        val header = "📋 *Список закупок* (страница ${currentPage + 1} из $totalPages):\n\n"
        val items = pageProcurements.joinToString("\n\n") { procurement ->
            formatProcurementItem(procurement)
        }
        
        val footer = "\n\nВсего закупок: ${procurements.size}"
        
        return header + items + footer
    }

    private fun createCombinedKeyboard(totalItems: Int, currentPage: Int, pageProcurements: List<Procurement>, startIndex: Int): InlineKeyboardMarkup {
        val keyboard = mutableListOf<MutableList<InlineKeyboardButton>>()
        
        // Кнопки навигации по страницам
        if (totalItems > ITEMS_PER_PAGE) {
            val totalPages = (totalItems + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
            val navigationRow = mutableListOf<InlineKeyboardButton>()
            
            // Кнопка "Предыдущая"
            if (currentPage > 0) {
                val prevButton = InlineKeyboardButton()
                prevButton.text = "⬅️ Предыдущая"
                prevButton.callbackData = "page_${currentPage - 1}"
                navigationRow.add(prevButton)
            }
            
            // Кнопка "Следующая"
            if (currentPage < totalPages - 1) {
                val nextButton = InlineKeyboardButton()
                nextButton.text = "Следующая ➡️"
                nextButton.callbackData = "page_${currentPage + 1}"
                navigationRow.add(nextButton)
            }
            
            if (navigationRow.isNotEmpty()) {
                keyboard.add(navigationRow)
            }
        }
        
        // Кнопки для каждой закупки на текущей странице
        pageProcurements.forEachIndexed { index, procurement ->
            val button = InlineKeyboardButton()
            button.text = "📋 ${startIndex + index + 1}. ${truncate(procurement.name, 25)}"
            button.callbackData = "procurement_${procurement.id}"
            keyboard.add(mutableListOf(button))
        }
        
        // Кнопки фильтров
        val filterRow = mutableListOf<InlineKeyboardButton>()
        val priceButton = InlineKeyboardButton()
        priceButton.text = "🔍 По цене"
        priceButton.callbackData = "filter_price"
        filterRow.add(priceButton)
        
        val dateButton = InlineKeyboardButton()
        dateButton.text = "📅 По дате"
        dateButton.callbackData = "filter_date"
        filterRow.add(dateButton)
        keyboard.add(filterRow)
        
        // Кнопка обновления
        val refreshRow = mutableListOf<InlineKeyboardButton>()
        val refreshButton = InlineKeyboardButton()
        refreshButton.text = "🔄 Обновить"
        refreshButton.callbackData = "refresh"
        refreshRow.add(refreshButton)
        keyboard.add(refreshRow)
        
        val markup = InlineKeyboardMarkup()
        markup.keyboard = keyboard
        return markup
    }

    private fun formatProcurementsAsTable(procurements: List<Procurement>): String {
        if (procurements.isEmpty()) {
            return "📋 Нет сохраненных закупок.\n\nИспользуйте браузерное расширение для добавления закупок в список."
        }

        val maxItems = 10 // Ограничиваем количество для Telegram
        val limitedProcurements = procurements.take(maxItems)
        
        val header = "📋 *Список закупок:*\n\n"
        val items = limitedProcurements.joinToString("\n\n") { procurement ->
            formatProcurementItem(procurement)
        }
        
        val footer = if (procurements.size > maxItems) {
            "\n\n... и еще ${procurements.size - maxItems} закупок"
        } else ""
        
        return header + items + footer
    }

    private fun formatProcurementItem(procurement: Procurement): String {
        return buildString {
            append("🏢 *${truncate(procurement.name, 50)}*")
            append("\n📄 ФЗ: ${procurement.federalLawNumber}")
            append("\n🔢 Реестровый номер: ${procurement.registryNumber}")
            append("\n💰 Цена: ${formatPrice(procurement.price)}")
            append("\n🏛️ Заказчик: ${truncate(procurement.publisher, 30)}")
            append("\n🔗 [Ссылка на закупку](${procurement.linkOnPlacement})")
        }
    }

    private fun truncate(text: String?, maxLength: Int): String {
        return text?.let {
            if (it.length <= maxLength) it else "${it.take(maxLength)}..."
        } ?: "Не указано"
    }

    private fun formatPrice(price: BigDecimal?): String {
        return price?.let {
            if (it > BigDecimal.ZERO) it.toString() else "Не указана"
        } ?: "Не указана"
    }
}