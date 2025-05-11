package ru.bjcreslin.naidizakupku.procurement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User
import java.math.BigDecimal

@Entity
@Table(
    name = "procurements",
    indexes = [Index(name = "idx_registry_number", columnList = "registryNumber", unique = true)]
)
class Procurement(

    @Column(name = "federal_law_number", nullable = false)
    val federalLawNumber: String,

    @Column(name = "link_placement", nullable = false)
    val linkOnPlacement: String,

    @Column(name = "registry_number", nullable = false)
    val registryNumber: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "publisher", nullable = false)
    val publisher: String,

    @Column(name = "price")
    val price: BigDecimal,

    @ManyToMany(mappedBy = "procurements", fetch = FetchType.LAZY)
    val users: MutableSet<User> = HashSet()
) : BaseEntity()