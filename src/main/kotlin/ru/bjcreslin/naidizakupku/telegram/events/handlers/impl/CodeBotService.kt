package ru.bjcreslin.naidizakupku.telegram.events.handlers.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.codeService.TelegramCodeService
import ru.bjcreslin.naidizakupku.telegram.events.handlers.CommandHandler
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService

@Service("/code")
class CodeBotService(
    private val telegramCodeService: TelegramCodeService,
    private val telegramUserService: TelegramUserService
) : CommandHandler {


    override fun execute(chatId: Long, params: String): String {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val code = telegramCodeService.createCode(user)
        val minutes = code.actionTimeMinutes
        val codeText =
            """Код для входа в сервис ${code.code}, действует ${minutes} ${getMinuteWord(minutes)}.""".trimIndent()
        return codeText
    }

    fun getMinuteWord(minutes: Long): String {
        val lastDigit = minutes % 10
        val lastTwoDigits = minutes % 100

        return when {
            lastTwoDigits in 11..14 -> "минут"
            lastDigit.toInt() == 1 -> "минута"
            lastDigit in 2..4 -> "минуты"
            else -> "минут"
        }
    }
}