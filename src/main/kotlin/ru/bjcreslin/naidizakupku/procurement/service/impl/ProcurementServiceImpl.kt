package ru.bjcreslin.naidizakupku.procurement.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.repository.ProcurementRepository
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService
import ru.bjcreslin.naidizakupku.user.entity.User

@Service
class ProcurementServiceImpl(
    private val procurementRepository: ProcurementRepository
) : ProcurementService {

    private val logger = LoggerFactory.getLogger(ProcurementServiceImpl::class.java)

    override fun getAllProcurementsForTelegram(user: User): List<Procurement> {
        return procurementRepository.findAllByUsers(setOf(user))
    }

    override fun saveProcurement(procurementDto: ProcurementDto, user: User): Procurement {
        val procurement = procurementRepository.findByRegistryNumber(procurementDto.registryNumber)
        return procurement?.apply {
            logger.info("User ${user.username} added procurement ${procurement.registryNumber} to himself")
            users.add(user)
        } ?: createProcurement(procurementDto, user).also {
            procurementRepository.save(it)
        }
    }

    private fun createProcurement(procurementDto: ProcurementDto, user: User): Procurement {
        return Procurement(
            procurementDto.registryNumber,
            linkOnPlacement = procurementDto.linkOnPlacement,
            registryNumber = procurementDto.registryNumber,
            name = procurementDto.name,
            publisher = procurementDto.publisher,
            price = procurementDto.price,
            users = mutableSetOf(user)
        ).also { logger.info("Created new procurement: $it") }
    }
}