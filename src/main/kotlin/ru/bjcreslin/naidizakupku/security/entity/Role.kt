package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity

/**
 * Роль пользователя
 */
@Entity
@Table(name = "roles")
data class Role(

    @Column(name = "name", unique = true, nullable = false)
    val name: String,

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userRoles: MutableList<UserRole> = mutableListOf()
) : BaseEntity()
