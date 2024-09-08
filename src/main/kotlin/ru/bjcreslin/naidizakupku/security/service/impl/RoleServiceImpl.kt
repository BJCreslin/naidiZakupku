package ru.bjcreslin.naidizakupku.security.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.entity.Role
import ru.bjcreslin.naidizakupku.security.repository.RoleRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService

@Service
class RoleServiceImpl (roleRepository: RoleRepository): RoleService {

    override fun getDefaultRole(): Role {


    }
}