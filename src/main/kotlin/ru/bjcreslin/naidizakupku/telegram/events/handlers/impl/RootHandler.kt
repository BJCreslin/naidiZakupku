package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState
import ru.bjcreslin.naidizakupku.telegram.state.service.TelegramStateService

@Service("root")
class RootHandler (val telegramStateService: TelegramStateService): CommandHandler{

    override fun execute(chatId: Long, params: String): String {
        telegramStateService.setState(chatId, SectionState.ROOT)
        return "Перехожу в главное меню"
    }

    override fun getSupportedState(): SectionState {
        return SectionState.ROOT
    }
}