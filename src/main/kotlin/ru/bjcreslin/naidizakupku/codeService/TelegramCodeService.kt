package ru.bjcreslin.naidizakupku.codeService

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.codeService.dto.TelegramCodeDto
import ru.bjcreslin.naidizakupku.user.entity.User

interface TelegramCodeService {

    /**
     * Создать код для пользователя
     */
    @Transactional
    fun createCode(user: User): TelegramCodeDto

    /**
     * Получить пользователя по коду
     */
    @Transactional
    fun getUserByCode(code: Int): User?

}