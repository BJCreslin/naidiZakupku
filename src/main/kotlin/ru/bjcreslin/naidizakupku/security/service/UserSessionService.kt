package ru.bjcreslin.naidizakupku.security.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.dto.TelegramUserInfo
import ru.bjcreslin.naidizakupku.security.entity.UserSession
import ru.bjcreslin.naidizakupku.security.repository.UserSessionRepository
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser
import ru.bjcreslin.naidizakupku.telegramUser.repository.TelegramUserRepository
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@Service
class UserSessionService(
    private val userSessionRepository: UserSessionRepository,
    private val telegramUserRepository: TelegramUserRepository,
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    
    private val logger = LoggerFactory.getLogger(UserSessionService::class.java)
    
    companion object {
        private const val SESSION_DURATION_HOURS = 24L
    }
    
    fun createSession(telegramUserInfo: TelegramUserInfo): UserSession {
        // Находим или создаем пользователя
        val user = findOrCreateUser(telegramUserInfo)
        
        // Создаем новую сессию
        val sessionId = generateSessionId()
        val expiresAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS)
        
        val session = UserSession(
            sessionId = sessionId,
            user = user,
            telegramId = telegramUserInfo.id,
            expiresAt = expiresAt,
            isActive = true
        )
        
        val savedSession = userSessionRepository.save(session)
        logger.info("Created session ${sessionId} for user ${telegramUserInfo.id}")
        
        return savedSession
    }
    
    fun validateSession(sessionId: String): UserSession? {
        val now = LocalDateTime.now()
        return userSessionRepository.findActiveSession(sessionId, now)
    }
    
    fun invalidateSession(sessionId: String): Boolean {
        val session = userSessionRepository.findBySessionId(sessionId)
        if (session != null) {
            session.isActive = false
            userSessionRepository.save(session)
            logger.info("Invalidated session: $sessionId")
            return true
        }
        return false
    }
    
    fun invalidateAllUserSessions(telegramId: Long): Int {
        val sessions = userSessionRepository.findByTelegramId(telegramId)
        sessions.forEach { it.isActive = false }
        userSessionRepository.saveAll(sessions)
        logger.info("Invalidated ${sessions.size} sessions for user $telegramId")
        return sessions.size
    }
    
    private fun findOrCreateUser(telegramUserInfo: TelegramUserInfo): User {
        // Сначала ищем существующего Telegram пользователя
        val telegramUser = telegramUserRepository.findByTelegramId(telegramUserInfo.id)
        
        if (telegramUser != null) {
            return telegramUser.user
        }
        
        // Создаем нового пользователя
        val username = telegramUserInfo.username ?: "user_${telegramUserInfo.id}"
        val user = User(
            username = username,
            enabled = true
        )
        
        val savedUser = userRepository.save(user)
        
        // Создаем связь с Telegram
        val newTelegramUser = TelegramUser(
            user = savedUser,
            telegramId = telegramUserInfo.id
        )
        
        telegramUserRepository.save(newTelegramUser)
        
        logger.info("Created new user for Telegram ID: ${telegramUserInfo.id}")
        return savedUser
    }
    
    private fun generateSessionId(): String {
        return UUID.randomUUID().toString()
    }
    
    fun createJwtToken(session: UserSession): String {
        val roles = session.user.userRoles.map { it.role.name }
        return jwtTokenProvider.createAccessToken(session.user.username, roles)
    }
    
    @Scheduled(fixedRate = 3600000) // Каждый час
    fun cleanupExpiredSessions() {
        val now = LocalDateTime.now()
        val expiredSessions = userSessionRepository.findExpiredSessions(now)
        
        if (expiredSessions.isNotEmpty()) {
            expiredSessions.forEach { it.isActive = false }
            userSessionRepository.saveAll(expiredSessions)
            logger.info("Cleaned up ${expiredSessions.size} expired sessions")
        }
    }
}
