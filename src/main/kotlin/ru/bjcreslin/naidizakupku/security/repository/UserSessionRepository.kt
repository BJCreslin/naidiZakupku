package ru.bjcreslin.naidizakupku.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.security.entity.UserSession
import java.time.LocalDateTime

@Repository
interface UserSessionRepository : JpaRepository<UserSession, Long> {
    
    fun findBySessionId(sessionId: String): UserSession?
    
    fun findByTelegramId(telegramId: Long): List<UserSession>
    
    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now AND us.isActive = true")
    fun findExpiredSessions(@Param("now") now: LocalDateTime): List<UserSession>
    
    @Query("SELECT us FROM UserSession us WHERE us.sessionId = :sessionId AND us.isActive = true AND us.expiresAt > :now")
    fun findActiveSession(@Param("sessionId") sessionId: String, @Param("now") now: LocalDateTime): UserSession?
}
