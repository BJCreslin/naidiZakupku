package ru.bjcreslin.naidizakupku.cfg

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * свойства ratelimiter
 */
@ConfigurationProperties(prefix = "rate-limit")
@Component
class RateLimiterConfiguration {
    @Value("\${max-requests}")
    val maxRequests: Int = 10

    @Value("\${period-minutes}")
    val periodMinutes: Long = 1
}