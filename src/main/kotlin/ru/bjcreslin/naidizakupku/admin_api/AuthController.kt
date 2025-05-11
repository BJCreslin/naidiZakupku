package ru.bjcreslin.naidizakupku.admin_api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CodeLoginRequest(val code: String)
data class JwtTokenResponse(val accessToken: String, val expiresIn: Long)

@RestController
@RequestMapping("/api/admin/v1/auth")
class AuthController {
    @PostMapping("/login-by-code")
    fun login(@RequestBody request: CodeLoginRequest): ResponseEntity<JwtTokenResponse> {
        // 1. Проверить код (сравнить с сохранёнными в Redis или в БД + TTL)
        // 2. Найти TelegramUser, связать с User
        // 3. Проверить, есть ли у него роль ADMIN
        // 4. Вернуть access token
        return ResponseEntity.ok(JwtTokenResponse("token", 10000))
    }
}