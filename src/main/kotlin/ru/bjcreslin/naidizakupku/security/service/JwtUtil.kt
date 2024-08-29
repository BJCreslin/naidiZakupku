package ru.bjcreslin.naidizakupku.security.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    lateinit var secret: String

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun validateToken(token: String, username: String): Boolean {
        val claims = getClaimsFromToken(token)
        return claims.subject == username && !isTokenExpired(token)
    }

    fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    fun isTokenExpired(token: String): Boolean {
        val claims = getClaimsFromToken(token)
        return claims.expiration.before(Date())
    }

}