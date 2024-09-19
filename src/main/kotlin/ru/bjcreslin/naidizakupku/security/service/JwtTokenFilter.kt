package ru.bjcreslin.naidizakupku.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {
    val LOGGING_WITH_TOKEN_NAME_S: kotlin.String = "Logging with token name: %s"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
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