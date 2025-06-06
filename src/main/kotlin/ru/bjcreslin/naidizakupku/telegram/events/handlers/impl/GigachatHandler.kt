package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.GigachatService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler

@Service("gigachat#prompt")
class GigachatHandler(
    val gigachatService: GigachatService
) : CommandHandler {
    override fun execute(chatId: Long, params: String): String {
        return gigachatService.getAnswer("Это тестовый запрос к гигачату. " +
                "Напиши четверостишье про кота и собаку" , chatId)!!
    }
}