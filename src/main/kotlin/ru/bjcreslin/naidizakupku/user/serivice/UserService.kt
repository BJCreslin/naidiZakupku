package ru.bjcreslin.naidizakupku.user.serivice

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.user.dto.UserDto
import ru.bjcreslin.naidizakupku.user.entity.User

interface UserService {

    /**
     * Поиск пользователя по имени
     */
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User?

    /**
     * Поиск пользователя по ID
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): User

    /**
     * Создание пользователя
     */
    @Transactional
    fun createUser(userDto: UserDto): User

    /**
     * Получение пользователя по токену
     */
    @Transactional(readOnly = true)
    fun findUserByUserDetails(jwtUser: JwtUser): User?
}