package ru.bjcreslin.naidizakupku.admin_api.dto

import org.mapstruct.Mapper
import ru.bjcreslin.naidizakupku.security.entity.Role

@Mapper(componentModel = "spring")
interface RolesMapper {

    fun toDto(role: Role): RoleDto
}