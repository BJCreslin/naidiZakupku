package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Entity
@Table(name = "users")
open class User(
    @Column(name = "username", unique = true, nullable = false)
    open var username: String,

    @Column(name = "password", nullable = true)
    open var password: String?,

    @Column(name = "enabled", nullable = false)
    open var enabled: Boolean,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open var userRoles: MutableList<UserRole> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var telegramUser: TelegramUser? = null,

    @ManyToMany
    @JoinTable(
        name = "user_procurement",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "procurement_id")]
    )
    open var procurements: MutableSet<Procurement> = mutableSetOf()
) : BaseEntity()
