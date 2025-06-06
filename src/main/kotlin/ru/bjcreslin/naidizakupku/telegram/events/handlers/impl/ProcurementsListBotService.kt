package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("root#list")
class ProcurementsListBotService (
    private val telegramUserService: TelegramUserService,
    private val procurementService: ProcurementService
): CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val procurements = procurementService.getAllProcurementsForTelegram(user)
        val formattedTable = formatProcurementsAsTable(procurements)
        return formattedTable
    }

    fun formatProcurementsAsTable(procurements: List<Procurement>): String {
        if (procurements.isEmpty()) {
            return "Нет сохраненных закупок."
        }
        val header = "| Federal Law | Placement Link | Registry Number | Name | Publisher | Price |n"
        val separator = "|---|---|---|---|---|---|n"
        val rows = procurements.joinToString("n") { procurement ->
            "| ${procurement.federalLawNumber} " +
                    "| ${procurement.linkOnPlacement} " +
                    "| ${procurement.registryNumber} " +
                    "| ${procurement.name} " +
                    "| ${procurement.publisher} " +
                    "| ${procurement.price} |"
        }
        return header + separator + rows
    }
}