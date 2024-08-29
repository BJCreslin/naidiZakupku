package ru.bjcreslin.naidizakupku.security.service

import ru.bjcreslin.naidizakupku.security.entity.User

interface UserService {
    fun saveUser(user: User): User

    fun findByUsername(username: String): User?
}