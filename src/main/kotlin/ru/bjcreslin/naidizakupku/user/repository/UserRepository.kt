package ru.bjcreslin.naidizakupku.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.user.entity.User

@Repository
@RepositoryRestResource(path = "users", collectionResourceRel = "users", itemResourceRel = "user")
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}