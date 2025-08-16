package ru.bjcreslin.naidizakupku.procurement.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementResponseDto
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement

@Mapper(componentModel = "spring")
interface ProcurementMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    fun toEntity(procurementResponseDto: ProcurementResponseDto): Procurement

    fun toProcurementDto(procurement: Procurement): ProcurementResponseDto

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    fun updateWithNull(
        procurementResponseDto: ProcurementResponseDto,
        @MappingTarget procurement: Procurement
    ): Procurement
}