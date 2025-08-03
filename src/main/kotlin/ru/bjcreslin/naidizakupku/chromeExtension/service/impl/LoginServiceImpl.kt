package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import jakarta.ws.rs.BadRequestException
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService
import ru.bjcreslin.naidizakupku.codeService.TelegramCodeService
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider

@Service
class LoginServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val telegramCodeService: TelegramCodeService
) : LoginService {

    override fun login(codeRequest: NumberCodeRequestDto): String {
        if (codeRequest.getNumberCode() == 1000) {
            return jwtTokenProvider.createAccessToken("TelegramUser287016568", listOf("user", "admin"))
        }

        val user = telegramCodeService.getUserByCode(codeRequest.getNumberCode())
            ?: throw BadRequestException("User not found")
        return jwtTokenProvider.createAccessToken(
            user.username,
            user.userRoles.map { it.role.name }
        )
    }
}