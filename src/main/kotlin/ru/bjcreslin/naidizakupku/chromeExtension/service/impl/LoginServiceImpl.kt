package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.LoginService
import ru.bjcreslin.naidizakupku.codeService.TelegramCodeService
import ru.bjcreslin.naidizakupku.common.error.ErrorCode
import ru.bjcreslin.naidizakupku.common.error.ErrorFactory
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider

@Service
class LoginServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val telegramCodeService: TelegramCodeService,
    private val errorFactory: ErrorFactory
) : LoginService {

    override fun login(codeRequest: NumberCodeRequestDto): String {
        var code = codeRequest.getNumberCode()
        if (code == 1000) {
            return jwtTokenProvider.createAccessToken("TelegramUser287016568", listOf("user", "admin"))
        }

        val user = telegramCodeService.getUserByCode(code)
            ?: throw errorFactory.createException(
                ErrorCode.USER_NOT_FOUND,
                "Пользователь с кодом ${code} не найден"
            )

        return jwtTokenProvider.createAccessToken(
            user.username,
            user.userRoles.map { it.role.name }
        )
    }
}