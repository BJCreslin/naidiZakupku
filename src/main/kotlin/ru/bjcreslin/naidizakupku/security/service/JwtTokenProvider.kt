package ru.bjcreslin.naidizakupku.security.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import ru.bjcreslin.naidizakupku.cfg.JwtProperties
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(private val jwtProperties: JwtProperties,
                       private val userDetailsService: UserDetailsService) {

    val rolesClaims: String = "role"
    private lateinit var codeSecret: Key

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
}