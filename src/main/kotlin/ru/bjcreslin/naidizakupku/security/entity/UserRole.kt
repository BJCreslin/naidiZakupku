package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User

/**
 * Таблица связанных с ролями пользователей.
 */
@Entity
@Table(name = "user_roles")
data class UserRole(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role
) : BaseEntity()