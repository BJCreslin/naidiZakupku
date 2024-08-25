package ru.bjcreslin.naidizakupku.chromeExtension.service

import ru.bjcreslin.naidizakupku.chromeExtension.dto.NumberCodeRequestDto

@SuppressWarnings("kotlin:S6517")
interface LoginService {

    /**
     * Сервис логирования
     * @param codeRequest номер переданный с фронта
     * @return jwt токен
     */
    fun login(codeRequest: NumberCodeRequestDto): String;
}