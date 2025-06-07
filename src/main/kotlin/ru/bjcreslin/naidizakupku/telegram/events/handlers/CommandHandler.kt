package ru.bjcreslin.naidizakupku.telegram.events.handlers

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

/**
 * Сервисы команд
 */
interface CommandHandler {

    @Transactional
    fun execute(chatId: Long, params: String): String

    fun getSupportedState(): SectionState
}