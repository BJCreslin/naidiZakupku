package ru.bjcreslin.naidizakupku.chromeExtension.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService

@RestController
@RequestMapping("api/chromeExtension/v1/login")
class LoginController(
    private val loginService: LoginService
) {

    @PostMapping("")
    fun login(
        @Valid @RequestBody codeRequest: NumberCodeRequestDto
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(loginService.login(codeRequest))
    }

}