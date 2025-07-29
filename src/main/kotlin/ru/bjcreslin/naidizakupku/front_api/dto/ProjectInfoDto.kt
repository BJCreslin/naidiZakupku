package ru.bjcreslin.naidizakupku.front_api.dto

import java.math.BigDecimal

/**
 * ДТО  синформацией о проекте
 */
data class ProjectInfoDto(
    /*** Количество закупок */
    val procurementsCount: Long,
    /*** Количество участников */
    val membersCount: Long,
    /*** Бюджет */
    val budgetAmount: BigDecimal,
)
