package ru.bjcreslin.naidizakupku.common

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object SecurityUtils {

    const val SYSTEM_USER_NAME = "system"

    /**
     * Получение текущего пользователя
     */
    fun getCurrentUsername(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is UserDetails) {
                return principal.username
            }
        }
        return SYSTEM_USER_NAME
    }
}