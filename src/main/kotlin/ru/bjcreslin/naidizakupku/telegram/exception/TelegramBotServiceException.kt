package ru.bjcreslin.naidizakupku.telegram.exception

import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class TelegramBotServiceException : RuntimeException {
    constructor(eMessage: String?, ex: TelegramApiException?) : super(eMessage, ex)

    constructor(eMessage: String?) : super(eMessage)

    companion object {
        const val ERROR_SEND_MESSAGE: String = "Error send message to user TelegramId %s with text %s"

        const val USER_MUST_HAVE_TELEGRAM_ID: String = "User with id %d must have Telegram Id."
    }
}