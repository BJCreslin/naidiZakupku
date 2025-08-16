package ru.bjcreslin.naidizakupku.cfg

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.concurrent.ConcurrentHashMap

@Configuration
class MetricsConfiguration {

    @Bean
    fun timedAspect(meterRegistry: MeterRegistry): TimedAspect {
        return TimedAspect(meterRegistry)
    }

    @Bean
    @Primary
    fun meterRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }

    @Bean
    fun customMetricsService(meterRegistry: MeterRegistry): CustomMetricsService {
        return CustomMetricsService(meterRegistry)
    }
}

class CustomMetricsService(private val meterRegistry: MeterRegistry) {
    
    private val timers = ConcurrentHashMap<String, Timer>()
    private val counters = ConcurrentHashMap<String, io.micrometer.core.instrument.Counter>()
    
    fun recordTelegramUpdateProcessingTime(updateId: Long, processingTimeMs: Long) {
        val timer = timers.computeIfAbsent("telegram.update.processing.time") {
            Timer.builder("telegram.update.processing.time")
                .description("Time spent processing Telegram updates")
                .register(meterRegistry)
        }
        timer.record(processingTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun recordGigaChatRequestTime(model: String, processingTimeMs: Long) {
        val timer = timers.computeIfAbsent("gigachat.request.time") {
            Timer.builder("gigachat.request.time")
                .description("Time spent on GigaChat API requests")
                .tag("model", model)
                .register(meterRegistry)
        }
        timer.record(processingTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun incrementTelegramCommandCounter(command: String) {
        val counter = counters.computeIfAbsent("telegram.commands.${command}") {
            io.micrometer.core.instrument.Counter.builder("telegram.commands")
                .description("Number of Telegram commands executed")
                .tag("command", command)
                .register(meterRegistry)
        }
        counter.increment()
    }
    
    fun incrementApiRequestCounter(endpoint: String, status: Int) {
        val counter = counters.computeIfAbsent("api.requests.${endpoint}.${status}") {
            io.micrometer.core.instrument.Counter.builder("api.requests")
                .description("Number of API requests")
                .tag("endpoint", endpoint)
                .tag("status", status.toString())
                .register(meterRegistry)
        }
        counter.increment()
    }
    
    fun recordDatabaseQueryTime(table: String, queryTimeMs: Long) {
        val timer = timers.computeIfAbsent("database.query.time") {
            Timer.builder("database.query.time")
                .description("Time spent on database queries")
                .tag("table", table)
                .register(meterRegistry)
        }
        timer.record(queryTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun incrementCacheHit(cacheName: String) {
        val counter = counters.computeIfAbsent("cache.hits.${cacheName}") {
            io.micrometer.core.instrument.Counter.builder("cache.hits")
                .description("Cache hit count")
                .tag("cache", cacheName)
                .register(meterRegistry)
        }
        counter.increment()
    }
    
    fun incrementCacheMiss(cacheName: String) {
        val counter = counters.computeIfAbsent("cache.misses.${cacheName}") {
            io.micrometer.core.instrument.Counter.builder("cache.misses")
                .description("Cache miss count")
                .tag("cache", cacheName)
                .register(meterRegistry)
        }
        counter.increment()
    }
    
    fun recordJwtTokenValidationTime(validationTimeMs: Long) {
        val timer = timers.computeIfAbsent("jwt.validation.time") {
            Timer.builder("jwt.validation.time")
                .description("Time spent validating JWT tokens")
                .register(meterRegistry)
        }
        timer.record(validationTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun incrementRateLimitExceeded() {
        val counter = counters.computeIfAbsent("rate.limit.exceeded") {
            io.micrometer.core.instrument.Counter.builder("rate.limit.exceeded")
                .description("Number of rate limit violations")
                .register(meterRegistry)
        }
        counter.increment()
    }
    
    fun recordNewsParsingTime(source: String, parsingTimeMs: Long) {
        val timer = timers.computeIfAbsent("news.parsing.time") {
            Timer.builder("news.parsing.time")
                .description("Time spent parsing news from RSS sources")
                .tag("source", source)
                .register(meterRegistry)
        }
        timer.record(parsingTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun incrementProcurementSearchCounter(query: String) {
        val counter = counters.computeIfAbsent("procurement.searches") {
            io.micrometer.core.instrument.Counter.builder("procurement.searches")
                .description("Number of procurement searches")
                .tag("query", query)
                .register(meterRegistry)
        }
        counter.increment()
    }
}
