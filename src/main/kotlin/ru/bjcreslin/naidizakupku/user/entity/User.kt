package ru.bjcreslin.naidizakupku.user.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.security.entity.Role

@Entity
@Table(name = "users")
public data class User(
    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "password", nullable = true)
    val password: String?,

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: List<Role>
): BaseEntity()
