package ru.bjcreslin.naidizakupku.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.security.entity.TelegramUser

@Repository
interface TelegramUserRepository : JpaRepository<TelegramUser, Long> {
}