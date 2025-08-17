package ru.bjcreslin.naidizakupku.security.ratelimiter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class AuthRateLimitingFilter : OncePerRequestFilter() {
    
    private val logger = LoggerFactory.getLogger(AuthRateLimitingFilter::class.java)
    
    companion object {
        private const val MAX_REQUESTS_PER_MINUTE: Long = 10L
        private const val WINDOW_MINUTES: Long = 1L
    }
    
    private val requestCounts = ConcurrentHashMap<String, MutableList<LocalDateTime>>()
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        
        // Применяем rate limiting только к auth endpoints
        if (path.startsWith("/api/auth/telegram") || path.startsWith("/api/auth/telegram-bot")) {
            val clientIp = getClientIp(request)
            
            if (!isAllowed(clientIp)) {
                logger.warn("Rate limit exceeded for IP: $clientIp")
                response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                response.writer.write("{\"error\":\"Rate limit exceeded. Please try again later.\"}")
                response.contentType = "application/json"
                return
            }
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun isAllowed(clientIp: String): Boolean {
        val now = LocalDateTime.now()
        val windowStart = now.minus(WINDOW_MINUTES, ChronoUnit.MINUTES)
        
        val requests = requestCounts.computeIfAbsent(clientIp) { mutableListOf() }
        
        // Удаляем старые запросы
        requests.removeAll { it.isBefore(windowStart) }
        
        // Проверяем лимит
        if (requests.size.toLong() >= MAX_REQUESTS_PER_MINUTE) {
            return false
        }
        
        // Добавляем текущий запрос
        requests.add(now)
        return true
    }
    
    private fun getClientIp(request: HttpServletRequest): String {
        var ip = request.getHeader("X-Forwarded-For")
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_FORWARDED")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_VIA")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("REMOTE_ADDR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        
        // Если IP содержит несколько адресов (через прокси), берем первый
        return if (ip.contains(",")) {
            ip.split(",")[0].trim()
        } else {
            ip
        }
    }
}
