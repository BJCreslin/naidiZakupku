package ru.bjcreslin.naidizakupku.procurement.dto

import ru.bjcreslin.naidizakupku.user.dto.UserDto
import java.math.BigDecimal

/**
 * DTO for {@link ru.bjcreslin.naidizakupku.procurement.entity.Procurement}
 */
data class ProcurementResponseDto(
    var id: Long = 0,
    var createdBy: String? = null,
    var updatedBy: String? = null,
    var comment: String? = null,
    var federalLawNumber: String?,
    var linkOnPlacement: String?,
    var registryNumber: String?,
    var name: String?,
    var publisher: String?,
    var price: BigDecimal?,
    var users: MutableSet<UserDto> = HashSet()
)