package ru.bjcreslin.naidizakupku.procutement.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.user.entity.User

@Repository
interface ProcurementRepository : JpaRepository<Procurement, Long>, JpaSpecificationExecutor<Procurement> {

    fun findAllByUsers(users: Set<User>): List<Procurement>

    fun findByRegistryNumber(registryNumber: String): Procurement?
}