package ru.bjcreslin.naidizakupku.cfg

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration(
    private val customMetricsService: CustomMetricsService
) {

    @Bean
    @Primary
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(200)
                .recordStats()
        )
        
        // Настройка имен кэшей
        cacheManager.setCacheNames(
            listOf(
                "telegramUpdateCache",
                "gigachatSessionCache", 
                "telegramUserCache",
                "telegramStateCache",
                "procurementsListCache",
                "statsCache",
                "helpMessageCache",
                "ProjectInfoCache"
            )
        )
        
        // Добавляем listener для отслеживания hit/miss
        cacheManager.setCacheLoader { key ->
            customMetricsService.incrementCacheMiss(key.toString())
            null
        }
        
        return cacheManager
    }

    @Bean
    fun cacheMetricsInterceptor(): CacheMetricsInterceptor {
        return CacheMetricsInterceptor(customMetricsService)
    }
}

class CacheMetricsInterceptor(
    private val customMetricsService: CustomMetricsService
) {
    
    fun onCacheHit(cacheName: String, key: String) {
        customMetricsService.incrementCacheHit(cacheName)
    }
    
    fun onCacheMiss(cacheName: String, key: String) {
        customMetricsService.incrementCacheMiss(cacheName)
    }
}