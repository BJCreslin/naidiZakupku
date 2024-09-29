package ru.bjcreslin.naidizakupku.telegramUser.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Repository
interface TelegramUserRepository : JpaRepository<TelegramUser, Long> {

    fun findByTelegramId(chatId: Long): TelegramUser?
}