package ru.bjcreslin.naidizakupku.procutement.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.user.entity.User

interface ProcurementService {

    @Transactional
    fun getAllProcurementsForTelegram(user: User):List<Procurement>

    @Transactional
    fun saveProcurement(procurementDto: ProcurementDto, user: User):Procurement
}