package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider

@Service
class LoginServiceImpl(val jwtTokenProvider: JwtTokenProvider) : LoginService {

    override fun login(codeRequest: NumberCodeRequestDto): String {
        if (codeRequest.numberCode == 1000) {
            return jwtTokenProvider.createAccessToken("user", listOf("user", "admin"))
        }
        return ""
    }
}