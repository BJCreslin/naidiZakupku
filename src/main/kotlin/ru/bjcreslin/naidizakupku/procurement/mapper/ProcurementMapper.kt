package ru.bjcreslin.naidizakupku.procurement.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementResponseDto
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
abstract class ProcurementMapper {

    abstract fun toEntity(procurementResponseDto: ProcurementResponseDto): Procurement


    abstract fun toProcurementDto(procurement: Procurement): ProcurementResponseDto

    abstract fun updateWithNull(
        procurementResponseDto: ProcurementResponseDto,
        @MappingTarget procurement: Procurement
    ): Procurement
}