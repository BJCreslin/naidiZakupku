package ru.bjcreslin.naidizakupku.procurement.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.front_api.dto.ProjectInfoDto
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.user.entity.User

@Repository
interface ProcurementRepository : JpaRepository<Procurement, Long>, JpaSpecificationExecutor<Procurement> {

    fun findAllByUsers(users: Set<User>): List<Procurement>

    fun findByRegistryNumber(registryNumber: String): Procurement?

    @Query(
        """
        SELECT 
            COUNT(p.id) as procurementsCount,
            COUNT(DISTINCT u.id) as membersCount,
            COALESCE(SUM(p.price), 0) as budgetAmount
        FROM Procurement p
        LEFT JOIN p.users u
        """
    )
    fun getProjectInfo(): ProjectInfoDto
}