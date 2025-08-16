package ru.bjcreslin.naidizakupku.procurement.filter

import org.springframework.data.jpa.domain.Specification
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.user.entity.User
import java.math.BigDecimal

data class ProcurementFilter(
    val users: MutableSet<User> = HashSet(),
    val searchText: String? = null,
    val customerName: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null
) {

    fun toSpecification() = Specification.where(usersSpec())
        .and(searchTextSpec())
        .and(customerNameSpec())
        .and(priceRangeSpec())

    private fun usersSpec() = Specification<Procurement> { root, _, cb ->
        cb.equal(root.get<User>("users"), users)
    }

    private fun searchTextSpec() = if (!searchText.isNullOrBlank()) {
        Specification<Procurement> { root, _, cb ->
            cb.or(
                cb.like(cb.lower(root.get("name")), "%${searchText.lowercase()}%"),
                cb.like(cb.lower(root.get("registryNumber")), "%${searchText.lowercase()}%")
            )
        }
    } else {
        Specification.where(null)
    }

    private fun customerNameSpec() = if (!customerName.isNullOrBlank()) {
        Specification<Procurement> { root, _, cb ->
            cb.like(cb.lower(root.get("publisher")), "%${customerName.lowercase()}%")
        }
    } else {
        Specification.where(null)
    }

    private fun priceRangeSpec() = when {
        minPrice != null && maxPrice != null -> {
            Specification<Procurement> { root, _, cb ->
                cb.between(root.get("price"), minPrice, maxPrice)
            }
        }
        minPrice != null -> {
            Specification<Procurement> { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice)
            }
        }
        maxPrice != null -> {
            Specification<Procurement> { root, _, cb ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice)
            }
        }
        else -> Specification.where(null)
    }
}