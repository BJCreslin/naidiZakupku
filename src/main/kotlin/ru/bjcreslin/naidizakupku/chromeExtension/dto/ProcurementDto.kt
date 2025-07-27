package ru.bjcreslin.naidizakupku.chromeExtension.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import ru.bjcreslin.naidizakupku.common.deserializer.BigDecimalWithSpaceDeserializer
import java.math.BigDecimal

data class ProcurementDto(

    @field:NotBlank(message = "Federal law number must not be blank")
    val federalLawNumber: String,

    @field:NotBlank(message = "Link placement must not be blank")
    val linkOnPlacement: String,

    @field:NotBlank(message = "Registry number must not be blank")
    val registryNumber: String,

    @field:NotBlank(message = "Name must not be blank")
    val name: String,

    @field:NotBlank(message = "Publisher must not be blank")
    val publisher: String,

    @JsonDeserialize(using = BigDecimalWithSpaceDeserializer::class)
    val price: BigDecimal,
)
