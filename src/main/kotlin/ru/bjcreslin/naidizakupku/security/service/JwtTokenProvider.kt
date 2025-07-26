package ru.bjcreslin.naidizakupku.security.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import ru.bjcreslin.naidizakupku.cfg.JwtPropertiesConfiguration
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidTokenException
import ru.bjcreslin.naidizakupku.security.exceptions.UnauthorizedException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtPropertiesConfiguration: JwtPropertiesConfiguration,
    private val userDetailsService: UserDetailsService
) {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private lateinit var codeSecret: SecretKey

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        codeSecret = Keys.hmacShaKeyFor(jwtPropertiesConfiguration.secret.toByteArray(StandardCharsets.UTF_8))
    }

    private fun getExpirationDate(currentDate: Date): Date {
        return Date(currentDate.time + jwtPropertiesConfiguration.expired * 1000)
    }

    fun createAccessToken(userName: String, roles: List<String>): String {
        val currentDate = Date()
        return Jwts.builder().claims()
            .subject(userName)
            .expiration(getExpirationDate(currentDate))
            .issuedAt(currentDate)
            .add("role", roles)
            .and()
            .signWith(codeSecret)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUsername(token: String?): String {
        return Jwts.parser()
            .verifyWith(codeSecret).build()  // Changed from decryptWith to verifyWith
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(jwtPropertiesConfiguration.header)
        if (bearerToken != null && bearerToken.startsWith(jwtPropertiesConfiguration.bearerPrefix)) {
            return bearerToken.substring(jwtPropertiesConfiguration.bearerPrefix.length).trim()
        }
        return null
    }

    fun validateToken(token: String?): Boolean {
        if (token == null) {
            logger.error("Token is null")
            throw InvalidTokenException("Token is null")
        }
        try {
            val claims = Jwts.parser()
                .verifyWith(codeSecret).build()  // Changed from decryptWith to verifyWith
                .parseSignedClaims(token)
            logger.info("Token parsed successfully, expiration: ${claims.body.expiration}")
            return !claims.body.expiration.before(Date())
        } catch (ex: ExpiredJwtException) {
            logger.error("Token expired: ${token.substring(0, 20)}... (hashCode: ${token.hashCode()})")
            throw InvalidTokenException("Token expired")
        } catch (ex: JwtException) {
            logger.error("Invalid token: ${token.substring(0, 20)}... (hashCode: ${token.hashCode()})")
            throw InvalidTokenException("Invalid token")
        } catch (ex: Exception) {
            logger.error("Unauthorized request: ${token.substring(0, 20)}... (hashCode: ${token.hashCode()})")
            throw UnauthorizedException(ex.toString())
        }
    }
}