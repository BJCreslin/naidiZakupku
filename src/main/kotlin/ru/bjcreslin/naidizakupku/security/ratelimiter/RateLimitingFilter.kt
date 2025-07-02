package ru.bjcreslin.naidizakupku.security.ratelimiter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.bjcreslin.naidizakupku.cfg.RateLimiterConfiguration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class RateLimitingFilter(
    val rateLimiterConfiguration: RateLimiterConfiguration
) : OncePerRequestFilter(

) {
    private val requests = ConcurrentHashMap<String, Pair<Long, Int>>()  // IP -> (timestamp, count)
    private val maxRequests = rateLimiterConfiguration.maxRequests
    private val period = TimeUnit.MINUTES.toMillis(rateLimiterConfiguration.periodMinutes)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val ip = request.remoteAddr
        val now = System.currentTimeMillis()
        val entry = requests[ip]
        if (entry == null || now - entry.first > period) {
            requests[ip] = Pair(now, 1)
        } else {
            if (entry.second >= maxRequests) {
                response.status = 429
                response.writer.write("Too Many Requests")
                return
            }
            requests[ip] = Pair(entry.first, entry.second + 1)
        }
        filterChain.doFilter(request, response)
    }
}