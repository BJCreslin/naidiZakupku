package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.security.entity.Role
import ru.bjcreslin.naidizakupku.security.entity.TelegramUser

@Entity
@Table(name = "users")
 data class User(
    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "password", nullable = true)
    val password: String?,

    @Column(name = "enabled", nullable = false)
    val enabled: Boolean,

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: List<Role>,
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val telegramUser: TelegramUser? = null
): BaseEntity()
