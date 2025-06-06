package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.GigachatService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler

@Service("gigachat#model")
class GigachatModelHandler(
    val gigachatService: GigachatService
) : CommandHandler {
    override fun execute(chatId: Long, params: String): String {
        return gigachatService.getModels(chatId)!!.models().data().joinToString("\n")
    }
}