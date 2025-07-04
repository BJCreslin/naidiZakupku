package ru.bjcreslin.naidizakupku.gigachat.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GigachatSessionRepository : JpaRepository<GigachatSession, Long> {

    fun deleteAllByTelegramId(telegramId: Long)

    fun findByUserId(userId: Long): GigachatSession?
}