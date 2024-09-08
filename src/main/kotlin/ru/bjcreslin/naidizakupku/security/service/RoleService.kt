package ru.bjcreslin.naidizakupku.security.service

import ru.bjcreslin.naidizakupku.security.entity.Role

interface RoleService {

    /**
     * Получение роли по умолчанию
     */
    fun getDefaultRole(): Role
}