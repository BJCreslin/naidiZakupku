package ru.bjcreslin.naidizakupku.chromeExtension.dto

import java.math.BigDecimal

data class ProcurementDto(
    val federalLawNumber: String,
    val linkOnPlacement: String,
    val registryNumber: String,
    val name: String,
    val publisher: String,
    val price: BigDecimal,

    )
