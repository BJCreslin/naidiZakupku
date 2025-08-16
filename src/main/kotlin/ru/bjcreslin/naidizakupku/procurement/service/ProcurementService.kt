package ru.bjcreslin.naidizakupku.procurement.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.filter.ProcurementFilter
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementListResponse
import ru.bjcreslin.naidizakupku.user.entity.User

interface ProcurementService {

    @Transactional
    fun getAllProcurementsForTelegram(user: User): List<Procurement>

    @Transactional
    fun saveProcurement(procurementDto: ProcurementDto, user: User): Procurement

    @Transactional
    fun getProcurements(filter: ProcurementFilter): ProcurementListResponse
}