package ru.bjcreslin.naidizakupku.procurement.service.impl

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.repository.ProcurementRepository
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService
import ru.bjcreslin.naidizakupku.procurement.filter.ProcurementFilter
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementListResponse
import ru.bjcreslin.naidizakupku.procurement.mapper.ProcurementMapper
import ru.bjcreslin.naidizakupku.user.entity.User

@Service
class ProcurementServiceImpl(
    private val procurementRepository: ProcurementRepository,
    private val procurementMapper: ProcurementMapper
) : ProcurementService {

    private val logger = LoggerFactory.getLogger(ProcurementServiceImpl::class.java)

    override fun getAllProcurementsForTelegram(user: User): List<Procurement> {
        return procurementRepository.findAllByUsers(setOf(user))
    }

    /**
     * Сохраняет закупку для пользователя и сбрасывает кэш для этого пользователя
     * @param procurementDto данные закупки
     * @param user пользователь, для которого сохраняется закупка
     * @return сохраненная закупка
     */
    @CacheEvict(value = ["procurementsListCache"], allEntries = true)
    override fun saveProcurement(procurementDto: ProcurementDto, user: User): Procurement {
        val procurement = procurementRepository.findByRegistryNumber(procurementDto.registryNumber)
        return procurement?.apply {
            logger.info("User ${user.username} added procurement ${procurement.registryNumber} to himself")
            users.add(user)
        } ?: createProcurement(procurementDto, user).also {
            procurementRepository.save(it)
        }
    }

    /**
     * Получает список закупок пользователя с кэшированием
     * Кэш автоматически сбрасывается при добавлении новых закупок для пользователя
     * @param filter фильтр для поиска закупок
     * @param user пользователь, для которого возвращаются закупки
     * @return список закупок пользователя
     */
    @Cacheable(value = ["procurementsListCache"], key = "#user.id")
    override fun getProcurements(filter: ProcurementFilter, user: User): ProcurementListResponse {
        logger.info("Getting procurements for user ${user.username} with filter: $filter")
        
        // Получаем все закупки пользователя
        val userProcurements = procurementRepository.findAllByUsers(setOf(user))
        
        // Применяем фильтр к закупкам пользователя
        val filteredProcurements = userProcurements.filter { procurement ->
            filter.matches(procurement)
        }
        
        return ProcurementListResponse(
            procurements = filteredProcurements.map { procurementMapper.toProcurementDto(it) },
            totalCount = filteredProcurements.size.toLong()
        )
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