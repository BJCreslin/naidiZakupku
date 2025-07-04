package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSession
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSessionRepository
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Service
class GigachatSessionService(private val gigachatSessionRepository: GigachatSessionRepository) {
    private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    fun reset(telegramUserId: Long) {
        logger.debug("Resetting gigachat context for user $telegramUserId")
        gigachatSessionRepository.deleteAllByTelegramId(telegramUserId)
    }

    fun save(gigachatSession: GigachatSession) {
        logger.debug("Saving gigachat context for ${gigachatSession.telegramId}")
        gigachatSessionRepository.save(gigachatSession)
    }

    fun getSession(user: TelegramUser): GigachatSession? {
        logger.debug("Getting gigachat context for ${user.telegramId}")
        return gigachatSessionRepository.findByUserId(user.id)
    }
}