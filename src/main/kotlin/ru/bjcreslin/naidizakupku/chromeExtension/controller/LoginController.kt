package ru.bjcreslin.naidizakupku.chromeExtension.controller

import jakarta.validation.Valid
import jakarta.ws.rs.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = ["chrome-extension://*"])
class LoginController(
    private val loginService: LoginService,
    private val jwtService: JwtTokenProvider
) {
    
    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val BEARER_PREFIX_LENGTH = 7
    }

    @PostMapping("login")
    fun login(
        @Valid @RequestBody codeRequest: NumberCodeRequestDto
    ): ResponseEntity<String> {
        return try {
            ResponseEntity.ok().body(loginService.login(codeRequest))
        } catch (e: BadRequestException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed")
        }
    }

    @GetMapping("/verify-token")
    fun verifyToken(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<Map<String, Any>> {
        return try {
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("valid" to false))
            } else {
                val token = authHeader.substring(BEARER_PREFIX_LENGTH)
                if (jwtService.validateToken(token)) {
                    ResponseEntity.ok(mapOf("valid" to true))
                } else {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("valid" to false))
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("valid" to false))
        }
    }
}