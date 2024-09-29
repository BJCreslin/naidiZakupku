package ru.bjcreslin.naidizakupku.telegram.events.handlers

/**
 * Сервисы команд
 */
interface CommandHandler {

    fun execute(chatId: Long, params: String): String
}