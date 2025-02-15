package ru.bjcreslin.naidizakupku.telegram.events.handlers

import org.springframework.transaction.annotation.Transactional

/**
 * Сервисы команд
 */
interface CommandHandler {

    @Transactional
    fun execute(chatId: Long, params: String): String
}