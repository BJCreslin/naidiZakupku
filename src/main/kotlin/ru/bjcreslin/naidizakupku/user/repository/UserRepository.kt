package ru.bjcreslin.naidizakupku.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.user.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}