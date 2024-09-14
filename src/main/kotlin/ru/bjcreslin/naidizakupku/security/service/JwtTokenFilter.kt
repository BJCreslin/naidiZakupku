package ru.bjcreslin.naidizakupku.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.String

@Component
class JwtTokenFilter(val jwtTokenProvider: JwtTokenProvider): OncePerRequestFilter {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtTokenProvider.resolveToken(request)
        try {
            if (token != null && (jwtTokenProvider.validateToken(token))) {
                val auth = jwtTokenProvider.getAuthentication(token)
                if (auth != null) {
                    SecurityContextHolder.getContext().authentication = auth
                    if (logger.isDebugEnabled) {
                        logger.debug(
                            String.format(
                                JwtTokenFilter.LOGGING_WITH_TOKEN_NAME_S,
                                jwtTokenProvider.getUsername(token)
                            )
                        )
                    }
                }
            }
        } catch (e: JwtAuthenticationException) {
            SecurityContextHolder.clearContext()
            throw JwtAuthenticationException(JwtAuthenticationException.JWT_IS_INVALID)
        }
        filterChain.doFilter(request, response)
    }
}