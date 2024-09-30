package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.security.entity.Role
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Entity
@Table(name = "users")
data class User(
    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "password", nullable = true)
    val password: String?,

    @Column(name = "enabled", nullable = false)
    val enabled: Boolean,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userRoles: MutableList<UserRole> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val telegramUser: TelegramUser? = null
) : BaseEntity()
