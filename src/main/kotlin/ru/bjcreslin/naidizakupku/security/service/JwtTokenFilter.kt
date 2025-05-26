package ru.bjcreslin.naidizakupku.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {
    val LOGGING_WITH_TOKEN_NAME_S: String = "Logging with token name: %s"

    private val publicPaths = listOf(
        "/api/health",
        "/api/health/",
        "/api/v1/login",
        "/api/v1/login/",
        "/api/chromeExtension/v1/login"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // Пропускаем фильтр, если путь публичный
        if (publicPaths.any { path.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = jwtTokenProvider.resolveToken(request)
        try {
            token?.let {
                if (jwtTokenProvider.validateToken(token)) {
                    val auth = jwtTokenProvider.getAuthentication(token)
                    auth?.let {
                        SecurityContextHolder.getContext().authentication = auth
                        if (logger.isDebugEnabled) {
                            logger.debug(
                                String.format(
                                    LOGGING_WITH_TOKEN_NAME_S,
                                    jwtTokenProvider.getUsername(token)
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            throw e
        }
        filterChain.doFilter(request, response)
    }
}