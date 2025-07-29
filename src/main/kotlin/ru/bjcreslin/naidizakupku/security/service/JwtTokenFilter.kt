package ru.bjcreslin.naidizakupku.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {

    private val publicPathMatchers = listOf(
        AntPathRequestMatcher("/api/health"),
        AntPathRequestMatcher("/api/health/**"),
        AntPathRequestMatcher("/api/v1/login/**"),
        AntPathRequestMatcher("/api/v1//verify-token/**"),
        AntPathRequestMatcher("/api/chromeExtension/v1/login**"),
        AntPathRequestMatcher("/api/news**"),
        AntPathRequestMatcher("admin/common**")
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Разрешённые пути – без проверки токена
        if (publicPathMatchers.any { it.matches(request) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = jwtTokenProvider.resolveToken(request)
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val auth = jwtTokenProvider.getAuthentication(token)
                if (auth != null) {
                    SecurityContextHolder.getContext().authentication = auth
                }
            } else {
                SecurityContextHolder.clearContext()
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            logger.error("Error processing JWT token", e)
            throw e
        }

        filterChain.doFilter(request, response)
    }
}