package ru.bjcreslin.naidizakupku.front_api.service.impl

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementFrontDto
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListFrontResponse
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListRequest
import ru.bjcreslin.naidizakupku.front_api.dto.SortDirection
import ru.bjcreslin.naidizakupku.front_api.service.ProcurementFrontService
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.repository.ProcurementRepository
import ru.bjcreslin.naidizakupku.user.entity.User
import java.math.BigDecimal
import java.util.Comparator

@Service
class ProcurementFrontServiceImpl(
    private val procurementRepository: ProcurementRepository
) : ProcurementFrontService {

    private val logger = LoggerFactory.getLogger(ProcurementFrontServiceImpl::class.java)

    @Cacheable(value = ["procurementsFrontCache"], key = "#user.id + '_' + #request.hashCode()")
    override fun getProcurements(request: ProcurementListRequest, user: User): ProcurementListFrontResponse {
        logger.info("Getting procurements for user ${user.username} with request: $request")
        
        // Получаем все закупки пользователя
        val userProcurements = procurementRepository.findAllByUsers(setOf(user))
        
        // Применяем фильтры
        val filteredProcurements = userProcurements.filter { procurement ->
            matchesFilters(procurement, request)
        }
        
        // Применяем сортировку
        val sortedProcurements = sortProcurements(filteredProcurements, request.sortBy, request.sortDirection)
        
        // Применяем пагинацию
        val totalCount = sortedProcurements.size.toLong()
        val totalPages = ((totalCount + request.size - 1) / request.size).toInt()
        val startIndex = request.page * request.size
        val endIndex = minOf(startIndex + request.size, sortedProcurements.size)
        
        val pagedProcurements = if (startIndex < sortedProcurements.size) {
            sortedProcurements.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ProcurementListFrontResponse(
            procurements = pagedProcurements.map { toFrontDto(it) },
            totalCount = totalCount,
            page = request.page,
            size = request.size,
            totalPages = totalPages
        )
    }
    
    private fun matchesFilters(procurement: Procurement, request: ProcurementListRequest): Boolean {
        // Поиск по тексту
        if (!request.searchText.isNullOrBlank()) {
            val searchLower = request.searchText.lowercase()
            val nameMatches = procurement.name.lowercase().contains(searchLower)
            val registryMatches = procurement.registryNumber.lowercase().contains(searchLower)
            if (!nameMatches && !registryMatches) {
                return false
            }
        }
        
        // Фильтр по заказчику
        if (!request.customerName.isNullOrBlank()) {
            if (!procurement.publisher.lowercase().contains(request.customerName.lowercase())) {
                return false
            }
        }
        
        // Фильтр по цене
        procurement.price?.let { price ->
            when {
                request.minPrice != null && request.maxPrice != null -> {
                    if (price < request.minPrice || price > request.maxPrice) {
                        return false
                    }
                }
                request.minPrice != null -> {
                    if (price < request.minPrice) {
                        return false
                    }
                }
                request.maxPrice != null -> {
                    if (price > request.maxPrice) {
                        return false
                    }
                }
            }
        } ?: run {
            // Если цена не указана, но фильтр требует цену
            if (request.minPrice != null || request.maxPrice != null) {
                return false
            }
        }
        
        return true
    }
    
    private fun sortProcurements(
        procurements: List<Procurement>, 
        sortBy: String, 
        sortDirection: SortDirection
    ): List<Procurement> {
        val comparator = when (sortBy.lowercase()) {
            "name" -> compareBy<Procurement> { it.name }
            "publisher" -> compareBy<Procurement> { it.publisher }
            "price" -> compareBy<Procurement> { it.price ?: BigDecimal.ZERO }
            "registrynumber" -> compareBy<Procurement> { it.registryNumber }
            "federallawnumber" -> compareBy<Procurement> { it.federalLawNumber }
            "updatedat" -> compareBy<Procurement> { it.updatedAt }
            else -> compareBy<Procurement> { it.createdAt }
        }
        
        return if (sortDirection == SortDirection.DESC) {
            procurements.sortedWith(comparator.reversed())
        } else {
            procurements.sortedWith(comparator)
        }
    }
    
    private fun toFrontDto(procurement: Procurement): ProcurementFrontDto {
        return ProcurementFrontDto(
            id = procurement.id,
            registryNumber = procurement.registryNumber,
            name = procurement.name,
            publisher = procurement.publisher,
            price = procurement.price,
            linkOnPlacement = procurement.linkOnPlacement,
            federalLawNumber = procurement.federalLawNumber,
            createdAt = procurement.createdAt,
            updatedAt = procurement.updatedAt
        )
    }
}
