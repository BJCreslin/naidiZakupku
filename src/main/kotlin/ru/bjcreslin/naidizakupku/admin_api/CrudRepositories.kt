package ru.bjcreslin.naidizakupku.admin_api

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import ru.bjcreslin.naidizakupku.security.entity.Role
import ru.bjcreslin.naidizakupku.user.entity.User


@RepositoryRestResource(path = "/api/v1/users", collectionResourceRel = "users", itemResourceRel = "user")
interface UserRepository : CrudRepository<User, Long>

@RepositoryRestResource(path = "/api/v1/roles", collectionResourceRel = "roles", itemResourceRel = "role")
interface RoleRepository : CrudRepository<Role, Long>