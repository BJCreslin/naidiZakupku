package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser

@Entity
@Table(name = "users")
class User(
    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "password", nullable = true)
    val password: String?,

    @Column(name = "enabled", nullable = false)
    val enabled: Boolean,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userRoles: MutableList<UserRole> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val telegramUser: TelegramUser? = null,

    @ManyToMany
    @JoinTable(
        name = "user_procurement",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "procurement_id")]
    )
    val procurements: Set<Procurement> = HashSet()
) : BaseEntity()
