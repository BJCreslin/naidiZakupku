package ru.bjcreslin.naidizakupku.chromeExtension.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider

@RestController
@RequestMapping("api/v1")
class LoginController(
    private val loginService: LoginService,
    private val jwtService: JwtTokenProvider
) {

    @PostMapping("login")
    fun login(
        @Valid @RequestBody codeRequest: NumberCodeRequestDto
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(loginService.login(codeRequest))
    }

    @GetMapping("/verify-token")
    fun verifyToken(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<Void> {
        return try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            } else {
                val token = authHeader.substring(7)
                if (jwtService.validateToken(token)) {
                    ResponseEntity.ok().build()
                } else {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}