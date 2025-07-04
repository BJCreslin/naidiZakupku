package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.GigachatSessionService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

@Service("gigachat#context#reset")
class GigachatResetContextHandler(
    val gigachatSessionService: GigachatSessionService
) : CommandHandler {

    override fun execute(chatId: Long, params: String): String {
        gigachatSessionService.reset(chatId)
        return "Контекст был обнулен."
    }

    override fun getSupportedState(): SectionState {
        return SectionState.GIGACHAT
    }
}