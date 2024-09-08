package ru.bjcreslin.naidizakupku.security.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.user.entity.User

interface UserService {

    /**
     * Сохранение пользователя
     */
    @Transactional
    fun saveUser(user: User): User

    /**
     * Поиск пользователя по имени
     */
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User?
}