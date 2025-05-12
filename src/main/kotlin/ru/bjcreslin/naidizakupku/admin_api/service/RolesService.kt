package ru.bjcreslin.naidizakupku.admin_api.service

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.admin_api.dto.RoleDto
import ru.bjcreslin.naidizakupku.admin_api.dto.RolesMapper
import ru.bjcreslin.naidizakupku.security.repository.RoleRepository

@Service
class RolesService(
    private val roleRepository: RoleRepository, private val rolesMapper: RolesMapper
) {

    public fun getAll(): List<RoleDto> {
        val roles = roleRepository.findAll()
        return roles.stream().map { rolesMapper.toDto(it) }.toList()
    }
}