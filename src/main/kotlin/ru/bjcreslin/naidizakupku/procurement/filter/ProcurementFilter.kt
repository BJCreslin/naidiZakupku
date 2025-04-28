package ru.bjcreslin.naidizakupku.procurement.filter

import org.springframework.data.jpa.domain.Specification
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.user.entity.User

data class ProcurementFilter(val users: MutableSet<User> = HashSet()) {

    fun toSpecification() = Specification.where(usersSpec())
    private fun usersSpec() = Specification<Procurement> { root, _, cb ->
        cb.equal(root.get<User>("users"), users)
    }
}