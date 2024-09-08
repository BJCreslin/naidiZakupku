package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity

@Entity
@Table(name = "user_roles")
public data class Role(

    @Column(name = "name", unique = true, nullable = false)
    val name: String,

) : BaseEntity()
