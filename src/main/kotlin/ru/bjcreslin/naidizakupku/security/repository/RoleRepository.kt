package ru.bjcreslin.naidizakupku.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.security.entity.Role

@Repository
@RepositoryRestResource(path = "roles", collectionResourceRel = "roles", itemResourceRel = "role")
interface RoleRepository: JpaRepository<Role, Long> {

    fun findByName(name: String): Role?

}