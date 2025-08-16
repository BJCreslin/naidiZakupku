package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("root#stats")
class StatsBotService(
    private val telegramUserService: TelegramUserService,
    private val procurementService: ProcurementService
) : CommandHandler {

    @Cacheable(cacheNames = ["statsCache"], key = "#chatId")
    override fun execute(chatId: Long, params: String): String {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val procurements = procurementService.getAllProcurementsForTelegram(user)
        
        return buildStatsMessage(procurements)
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }

    private fun buildStatsMessage(procurements: List<ru.bjcreslin.naidizakupku.procurement.entity.Procurement>): String {
        val totalProcurements = procurements.size
        
        if (totalProcurements == 0) {
            return """
                📊 *Статистика*
                
                У вас пока нет сохраненных закупок.
                Используйте браузерное расширение для добавления закупок!
            """.trimIndent()
        }

        val totalPrice = procurements.sumOf { 
            it.price?.toLong() ?: 0L 
        }
        
        val avgPrice = if (totalProcurements > 0) totalPrice / totalProcurements else 0L
        
        val topPublishers = procurements
            .groupBy { it.publisher }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(3)

        return """
            📊 *Статистика по закупкам*
            
            📈 *Общая информация:*
            • Всего закупок: $totalProcurements
            • Общая стоимость: ${formatPrice(totalPrice.toString())}
            • Средняя стоимость: ${formatPrice(avgPrice.toString())}
            
            🏛️ *Топ заказчиков:*
            ${topPublishers.joinToString("\n") { "• ${it.first ?: "Не указан"}: ${it.second} закупок" }}
            
            💡 *Рекомендации:*
            • Используйте /list для просмотра всех закупок
            • Анализируйте закупки с помощью AI (/gigachat)
        """.trimIndent()
    }

    private fun formatPrice(price: String): String {
        return if (price.isNotBlank() && price != "0") {
            "${price.toLongOrNull()?.let { "%,d".format(it) } ?: price} ₽"
        } else {
            "Не указана"
        }
    }

    @CacheEvict(cacheNames = ["statsCache"], key = "#chatId")
    fun invalidateUserStats(chatId: Long) {
        // Метод для инвалидации кэша статистики пользователя
    }
}
