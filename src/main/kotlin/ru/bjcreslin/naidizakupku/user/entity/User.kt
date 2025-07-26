package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Entity
@Table(name = "users")
class User(
    @Column(name = "username", unique = true, nullable = false)
    var username: String,

    @Column(name = "password", nullable = true)
    var password: String?,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userRoles: MutableList<UserRole> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var telegramUser: TelegramUser? = null,

    @ManyToMany
    @JoinTable(
        name = "user_procurement",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "procurement_id")]
    )
    val procurements: MutableSet<Procurement> = mutableSetOf()
) : BaseEntity()
