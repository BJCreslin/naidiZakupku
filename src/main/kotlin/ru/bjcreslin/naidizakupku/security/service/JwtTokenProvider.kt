package ru.bjcreslin.naidizakupku.security.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import ru.bjcreslin.naidizakupku.cfg.JwtProperties
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidTokenException
import ru.bjcreslin.naidizakupku.security.exceptions.UnauthorizedException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val userDetailsService: UserDetailsService
) {

    val rolesClaims: String = "role"
    private lateinit var codeSecret: SecretKey

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        codeSecret = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }

    private fun getExpirationDate(currentDate: Date): Date {
        return Date(currentDate.time + jwtProperties.expired * 1000)
    }

    fun createAccessToken(userName: String, roles: List<String>): String {
        val claims = Jwts.claims().subject(userName).build()
        claims[rolesClaims] = roles
        val currentDate = Date()
        return Jwts.builder().claims()
            .subject(userName)
            .expiration(getExpirationDate(currentDate))
            .issuedAt(currentDate)
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
            .decryptWith(codeSecret).build()
            .parseSignedClaims(token)
            .payload
            .subject;
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(jwtProperties.header)
        if (bearerToken != null && bearerToken.startsWith(jwtProperties.bearerPrefix)) {
            return bearerToken.substring(jwtProperties.bearerPrefix.length)
        }
        return null
    }

    fun validateToken(token: String?): Boolean {
        if (token == null) {
            throw InvalidTokenException("Token is null")
        }
        try {
            val claims = Jwts.parser()
                .decryptWith(codeSecret).build()
                .parseSignedClaims(token)
            return !claims.payload.expiration.before(Date())
        } catch (ex: ExpiredJwtException) {
            throw InvalidTokenException("Token expired")
        } catch (ex: JwtException) {
            throw InvalidTokenException("Invalid token")
        } catch (ex: Exception) {
            throw UnauthorizedException("Unauthorized")
        }
    }
}