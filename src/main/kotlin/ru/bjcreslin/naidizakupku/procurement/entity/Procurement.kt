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
open class Procurement(

    @Column(name = "federal_law_number", nullable = false)
    open var federalLawNumber: String,

    @Column(name = "link_placement", nullable = false)
    open var linkOnPlacement: String,

    @Column(name = "registry_number", nullable = false)
    open var registryNumber: String,

    @Column(name = "name", nullable = false)
    open var name: String,

    @Column(name = "publisher", nullable = false)
    open var publisher: String,

    @Column(name = "price")
    open var price: BigDecimal,

    @ManyToMany(mappedBy = "procurements", fetch = FetchType.LAZY)
    open var users: MutableSet<User> = HashSet()
) : BaseEntity()