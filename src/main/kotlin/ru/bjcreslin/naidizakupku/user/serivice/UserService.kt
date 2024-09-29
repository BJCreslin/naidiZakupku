package ru.bjcreslin.naidizakupku.user.serivice

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.user.dto.UserDto
import ru.bjcreslin.naidizakupku.user.entity.User

interface UserService {

    /**
     * Сохранение пользователя
     */
    @Transactional
    fun saveNewDefaultUser(user: User): User

    /**
     * Поиск пользователя по имени
     */
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User?

    /**
     * Создание пользователя
     */
    @Transactional
    fun createUser(userDto: UserDto): User
}