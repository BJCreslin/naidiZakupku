package ru.bjcreslin.naidizakupku.front_api.dto

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * DTO для отображения закупок на фронте
 */
data class ProcurementFrontDto(
    val id: Long,
    val registryNumber: String,
    val name: String,
    val publisher: String,
    val price: BigDecimal?,
    val linkOnPlacement: String,
    val federalLawNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Запрос на получение закупок с фильтрацией и сортировкой
 */
data class ProcurementListRequest(
    val searchText: String? = null,
    val customerName: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val sortBy: String = "createdAt",
    val sortDirection: SortDirection = SortDirection.DESC,
    val page: Int = 0,
    val size: Int = 20
)

/**
 * Направление сортировки
 */
enum class SortDirection {
    ASC, DESC
}

/**
 * Ответ с закупками для фронта
 */
data class ProcurementListFrontResponse(
    val procurements: List<ProcurementFrontDto>,
    val totalCount: Long,
    val page: Int,
    val size: Int,
    val totalPages: Int
)
