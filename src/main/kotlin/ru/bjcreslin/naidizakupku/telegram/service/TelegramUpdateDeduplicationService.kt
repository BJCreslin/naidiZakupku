package ru.bjcreslin.naidizakupku.telegram.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * Сервис для дедупликации обновлений Telegram по updateId
 */
@Service
class TelegramUpdateDeduplicationService {

    /**
     * Проверяет, был ли уже обработан update с данным ID
     * @param updateId ID обновления Telegram
     * @return true если update уже был обработан
     */
    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun isUpdateProcessed(@Suppress("UNUSED_PARAMETER") updateId: Int): Boolean {
        // При первом вызове возвращаем false и кэшируем результат
        return false
    }

    /**
     * Отмечает update как обработанный
     * @param updateId ID обновления Telegram
     * @return true (всегда, для кэширования)
     */
    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun markAsProcessed(@Suppress("UNUSED_PARAMETER") updateId: Int): Boolean {
        // Возвращаем true и кэшируем результат
        return true
    }
}
