package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService

@Service
class LoginServiceImpl : LoginService {
    override fun login(codeRequest: NumberCodeRequestDto): String {
        if (codeRequest.numberCode == 1000) {
            return "JWTtoken"
        }
        return ""
    }
}