package ru.bjcreslin.naidizakupku.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService

@Component
class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customMetricsService: CustomMetricsService
) : OncePerRequestFilter() {

    private val publicPathMatchers = listOf(
        AntPathRequestMatcher("/api/health"),
        AntPathRequestMatcher("/api/health/**"),
        AntPathRequestMatcher("/api/v1/login/**"),
        AntPathRequestMatcher("/api/v1//verify-token/**"),
        AntPathRequestMatcher("/api/chromeExtension/v1/login**"),
        AntPathRequestMatcher("/api/news/**"),
        AntPathRequestMatcher("/admin/common/**"),
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val startTime = System.currentTimeMillis()
        
        try {
            val token = getTokenFromRequest(request)
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
            
            val validationTime = System.currentTimeMillis() - startTime
            customMetricsService.recordJwtTokenValidationTime(validationTime)
            
        } catch (e: Exception) {
            val validationTime = System.currentTimeMillis() - startTime
            customMetricsService.recordJwtTokenValidationTime(validationTime)
            // Логируем ошибку, но не прерываем цепочку фильтров
        }
        
        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}