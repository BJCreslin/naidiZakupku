package ru.bjcreslin.naidizakupku.chromeExtension.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto

@SuppressWarnings("kotlin:S6517")
interface LoginService {

    /**
     * Сервис логирования
     * @param codeRequest номер переданный с фронта
     * @return jwt токен
     */
    @Transactional
    fun login(codeRequest: NumberCodeRequestDto): String;
}