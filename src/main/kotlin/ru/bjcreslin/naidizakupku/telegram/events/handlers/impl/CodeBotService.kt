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
        val codeText =
            """Код для входа в сервис ${code.code}, действует ${code.actionTimeMinutes} минут.""".trimIndent()
        return codeText
    }
}