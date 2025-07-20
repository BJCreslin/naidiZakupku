package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * свойства ratelimiter
 */
@ConfigurationProperties(prefix = "rate-limit")
@Component
class RateLimiterConfiguration {
    val maxRequests: Int = 10

    val periodMinutes: Long = 1
}