package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSession
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSessionRepository
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Service
class GigachatSessionService(private val gigachatSessionRepository: GigachatSessionRepository) {
    private val Logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    fun reset(user: TelegramUser) {
        Logger.debug("Resetting gigachat context for user ${user.telegramId}")
        gigachatSessionRepository.deleteAllByTelegramId(user.telegramId)
    }

    fun save(gigachatSession: GigachatSession) {
        Logger.debug("Saving gigachat context for ${gigachatSession.telegramId}")
        gigachatSessionRepository.save(gigachatSession)
    }

    fun getSession(user: TelegramUser): GigachatSession? {
        Logger.debug("Getting gigachat context for ${user.telegramId}")
        return gigachatSessionRepository.findByUserId(user.id)
    }
}