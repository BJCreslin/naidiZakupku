package ru.bjcreslin.naidizakupku.procutement.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.procutement.entity.Procurement
import ru.bjcreslin.naidizakupku.procutement.repository.ProcurementRepository
import ru.bjcreslin.naidizakupku.procutement.service.ProcurementService
import ru.bjcreslin.naidizakupku.user.entity.User

@Service
class ProcurementServiceImpl(
    private val procurementRepository: ProcurementRepository
) : ProcurementService {

    override fun getAllProcurementsForTelegram(user: User): List<Procurement> {
        return procurementRepository.findAllByUsers(setOf(user))
    }
}