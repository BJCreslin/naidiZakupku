package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.BaseEntity

@Entity
@Table(name = "users")
class User: BaseEntity (
    @Column(name = "username", unique = true, nullable = false)
    val username: String,

    @Column(name = "password", nullable = true)
    val password: String?,

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private var roles: Set<Role?>? = HashSet()
)
