package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity

/**
 * Роль пользователя
 */
@Entity
@Table(name = "roles")
open class Role(

    @Column(name = "name", unique = true, nullable = false)
    open var name: String,

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var userRoles: MutableList<UserRole> = mutableListOf()
) : BaseEntity()
