package ru.bjcreslin.naidizakupku.telegram.state.service

import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

interface TelegramStateService {

    fun getState(chatID: Long): SectionState

    fun getCommandHandlerKey(chatID: Long, messageText: String): String
}