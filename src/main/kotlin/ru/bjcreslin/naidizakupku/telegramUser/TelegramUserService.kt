package ru.bjcreslin.naidizakupku.telegramUser

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.user.entity.User

interface TelegramUserService {

    @Transactional
    fun getNewOrSavedUserByTelegramId(telegramId: Long): User
}