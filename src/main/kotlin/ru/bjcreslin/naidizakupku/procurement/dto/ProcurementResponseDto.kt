package ru.bjcreslin.naidizakupku.procurement.dto

import java.math.BigDecimal

/**
 * DTO for {@link ru.bjcreslin.naidizakupku.procurement.entity.Procurement}
 * Не содержит информацию о пользователях для безопасности
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
    var price: BigDecimal?
) {
    companion object {
        fun createListResponse(procurements: List<ProcurementResponseDto>, totalCount: Long): ProcurementListResponse {
            return ProcurementListResponse(procurements, totalCount)
        }
    }
}

/**
 * Response wrapper for list of procurements
 */
data class ProcurementListResponse(
    val procurements: List<ProcurementResponseDto>,
    val totalCount: Long
)