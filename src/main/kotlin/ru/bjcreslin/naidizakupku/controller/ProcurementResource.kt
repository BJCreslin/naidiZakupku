package ru.bjcreslin.naidizakupku.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import ru.bjcreslin.naidizakupku.procurement.dto.ProcurementListResponse
import ru.bjcreslin.naidizakupku.procurement.filter.ProcurementFilter
import ru.bjcreslin.naidizakupku.procurement.mapper.ProcurementMapper
import ru.bjcreslin.naidizakupku.procurement.entity.Procurement
import ru.bjcreslin.naidizakupku.procurement.repository.ProcurementRepository
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.repository.UserRepository
import java.io.IOException
import java.util.Optional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import ru.bjcreslin.naidizakupku.procurement.service.ProcurementService

@RestController
@RequestMapping("/api/v1/procurements")
class ProcurementResource(
    private val procurementService: ProcurementService,
    private val customMetricsService: CustomMetricsService,
    private val userRepository: UserRepository
) {

    /**
     * Получает список закупок для текущего пользователя
     * @param filter фильтр для поиска закупок
     * @return список закупок пользователя
     */
    @GetMapping
    fun getProcurements(@ModelAttribute filter: ProcurementFilter): ResponseEntity<ProcurementListResponse> {
        val startTime = System.currentTimeMillis()
        
        try {
            // Получаем текущего пользователя из контекста безопасности
            val currentUser = getCurrentUser()
            
            val result = procurementService.getProcurements(filter, currentUser)
            
            // Записываем метрики поиска
            val searchQuery = buildSearchQuery(filter)
            customMetricsService.incrementProcurementSearchCounter(searchQuery)
            
            val processingTime = System.currentTimeMillis() - startTime
            // Можно добавить метрику времени обработки поиска
            
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            // Логируем ошибку
            throw e
        }
    }

    /**
     * Получает текущего пользователя из контекста безопасности
     * @return текущий пользователь
     * @throws ResponseStatusException если пользователь не найден
     */
    private fun getCurrentUser(): User {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        val jwtUser = authentication?.principal as? JwtUser
        
        if (jwtUser == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
        }
        
        return userRepository.findByUsername(jwtUser.username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    private fun buildSearchQuery(filter: ProcurementFilter): String {
        val queryParts = mutableListOf<String>()
        
        if (!filter.searchText.isNullOrBlank()) {
            queryParts.add("text:${filter.searchText}")
        }
        if (!filter.customerName.isNullOrBlank()) {
            queryParts.add("customer:${filter.customerName}")
        }
        if (filter.minPrice != null) {
            queryParts.add("minPrice:${filter.minPrice}")
        }
        if (filter.maxPrice != null) {
            queryParts.add("maxPrice:${filter.maxPrice}")
        }
        
        return if (queryParts.isEmpty()) "empty" else queryParts.joinToString("|")
    }
}
