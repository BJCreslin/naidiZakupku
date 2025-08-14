package ru.bjcreslin.naidizakupku.cfg

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.boot.context.event.ApplicationReadyEvent

/**
 * Конфигурация мониторинга кэшей
 * Регистрирует метрики Caffeine кэшей в Micrometer
 */
@Configuration
class CacheMonitoringConfiguration(
    private val cacheManager: CacheManager,
    private val meterRegistry: MeterRegistry
) {

    /**
     * Регистрируем метрики кэшей после запуска приложения
     */
    @EventListener
    fun bindCacheMetrics(event: ApplicationReadyEvent) {
        cacheManager.cacheNames.forEach { cacheName ->
            val cache = cacheManager.getCache(cacheName)
            if (cache is CaffeineCache) {
                CaffeineCacheMetrics.monitor(meterRegistry, cache.nativeCache, cacheName)
            }
        }
    }
}
