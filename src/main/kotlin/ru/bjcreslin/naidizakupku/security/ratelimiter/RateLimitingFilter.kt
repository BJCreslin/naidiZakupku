package ru.bjcreslin.naidizakupku.security.ratelimiter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitingFilter(
    private val customMetricsService: CustomMetricsService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(RateLimitingFilter::class.java)
    private val requestCounts = ConcurrentHashMap<String, MutableList<LocalDateTime>>()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = getClientIp(request)
        val currentTime = LocalDateTime.now()
        
        // Очистка старых запросов (старше 1 минуты)
        cleanupOldRequests(clientIp, currentTime)
        
        // Проверка лимита запросов
        if (isRateLimitExceeded(clientIp, currentTime)) {
            logger.warn("Rate limit exceeded for IP: $clientIp")
            customMetricsService.incrementRateLimitExceeded()
            
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Rate limit exceeded. Please try again later.")
            return
        }
        
        // Добавление текущего запроса
        addRequest(clientIp, currentTime)
        
        // Продолжение обработки запроса
        filterChain.doFilter(request, response)
    }
    
    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (xForwardedFor != null && xForwardedFor.isNotEmpty()) {
            xForwardedFor.split(",")[0].trim()
        } else {
            request.remoteAddr
        }
    }
    
    private fun cleanupOldRequests(clientIp: String, currentTime: LocalDateTime) {
        val requests = requestCounts[clientIp] ?: return
        val oneMinuteAgo = currentTime.minusMinutes(1)
        
        requests.removeAll { it.isBefore(oneMinuteAgo) }
        
        if (requests.isEmpty()) {
            requestCounts.remove(clientIp)
        }
    }
    
    private fun isRateLimitExceeded(clientIp: String, currentTime: LocalDateTime): Boolean {
        val requests = requestCounts[clientIp] ?: return false
        val oneMinuteAgo = currentTime.minusMinutes(1)
        
        val recentRequests = requests.count { it.isAfter(oneMinuteAgo) }
        return recentRequests >= 10 // Максимум 10 запросов в минуту
    }
    
    private fun addRequest(clientIp: String, currentTime: LocalDateTime) {
        requestCounts.computeIfAbsent(clientIp) { mutableListOf() }.add(currentTime)
    }
}