package ru.bjcreslin.naidizakupku.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.security.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}