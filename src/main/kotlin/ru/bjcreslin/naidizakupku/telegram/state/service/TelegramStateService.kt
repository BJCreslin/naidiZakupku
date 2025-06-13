package ru.bjcreslin.naidizakupku.telegram.state.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.telegram.state.entity.SectionState

interface TelegramStateService {

    /*** Получить состояние */
    @Transactional(readOnly = true)
    fun getState(chatID: Long): SectionState

    /*** Получить ключ обработчика */
    fun getCommandHandlerKey(chatID: Long, messageText: String): String

    /*** Установить состояние */
    @Transactional
    fun setState(chatID: Long, sectionState: SectionState)
}