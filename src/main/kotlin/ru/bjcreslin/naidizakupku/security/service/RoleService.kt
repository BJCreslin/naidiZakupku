package ru.bjcreslin.naidizakupku.security.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.security.entity.Role

interface RoleService {

    /**
     * Получение роли по умолчанию
     */
    @Transactional(readOnly = true)
    fun getDefaultRole(): Role
}