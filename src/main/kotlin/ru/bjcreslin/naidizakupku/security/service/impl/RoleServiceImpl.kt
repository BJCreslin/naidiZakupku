package ru.bjcreslin.naidizakupku.security.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.UsersConfiguration
import ru.bjcreslin.naidizakupku.security.entity.Role
import ru.bjcreslin.naidizakupku.security.repository.RoleRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
    private val usersConfiguration: UsersConfiguration
) : RoleService {

    override fun getDefaultRole(): Role {
        return roleRepository.findByName(getDefaultRoleName())
            ?: throw Exception("Role not found in database ${getDefaultRoleName()}")
    }

    fun getDefaultRoleName(): String {
        return usersConfiguration.defaultRole
    }
}