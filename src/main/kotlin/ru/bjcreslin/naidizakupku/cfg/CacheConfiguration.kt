package ru.bjcreslin.naidizakupku.cfg

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager = CaffeineCacheManager().apply { 
        setCaffeine(caffeine())
        // Настраиваем кэши с разными параметрами
        setCacheNames(setOf("ProjectInfoCache", "gigachatSessionCache", "telegramUpdateCache"))
    }

    private fun caffeine() =
        Caffeine.newBuilder()
            .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
            .maximumSize(200)
            .recordStats() // Включаем сбор статистики для мониторинга
}