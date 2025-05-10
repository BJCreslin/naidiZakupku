package ru.bjcreslin.naidizakupku.procurement.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementResponseDto
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement

@Mapper(componentModel = "spring")
interface ProcurementMapper {

    fun toEntity(procurementResponseDto: ProcurementResponseDto): Procurement


    fun toProcurementDto(procurement: Procurement): ProcurementResponseDto

    fun updateWithNull(
        procurementResponseDto: ProcurementResponseDto,
        @MappingTarget procurement: Procurement
    ): Procurement
}