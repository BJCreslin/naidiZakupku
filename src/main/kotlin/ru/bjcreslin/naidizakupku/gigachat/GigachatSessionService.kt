package ru.bjcreslin.naidizakupku.gigachat

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSession
import ru.bjcreslin.naidizakupku.gigachat.session.GigachatSessionRepository
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Service
class GigachatSessionService(private val gigachatSessionRepository: GigachatSessionRepository) {
    private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @CacheEvict(cacheNames = ["gigachatSessionCache"], key = "#telegramId")
    fun reset(telegramId: Long) {
        logger.debug("Resetting gigachat context for user $telegramId")
        gigachatSessionRepository.deleteAllByTelegramId(telegramId)
    }

    @Cacheable(cacheNames = ["gigachatSessionCache"], key = "#gigachatSession.telegramId")
    fun save(gigachatSession: GigachatSession) {
        logger.debug("Saving gigachat context for ${gigachatSession.telegramId}")
        gigachatSessionRepository.save(gigachatSession)
    }

    @Cacheable(cacheNames = ["gigachatSessionCache"], key = "#user.telegramId")
    fun getSession(user: TelegramUser): GigachatSession? {
        logger.debug("Getting gigachat context for ${user.telegramId}")
        return gigachatSessionRepository.findByUserId(user.id)
    }
}