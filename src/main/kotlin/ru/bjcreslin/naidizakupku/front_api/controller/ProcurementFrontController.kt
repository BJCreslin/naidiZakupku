package ru.bjcreslin.naidizakupku.front_api.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListFrontResponse
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListRequest
import ru.bjcreslin.naidizakupku.front_api.service.ProcurementFrontService
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.user.serivice.UserService

/**
 * Контроллер для работы с закупками на фронте
 */
@RestController
@RequestMapping("api/front/procurements")
class ProcurementFrontController(
    private val procurementFrontService: ProcurementFrontService,
    private val userService: UserService
) {

    /**
     * Получает список закупок пользователя с фильтрацией, сортировкой и пагинацией
     * 
     * @param request параметры запроса (фильтры, сортировка, пагинация)
     * @param jwtUser аутентифицированный пользователь
     * @return список закупок с метаданными
     * 
     * Примеры запросов:
     * GET /api/front/procurements?searchText=строительство&sortBy=price&sortDirection=DESC&page=0&size=10
     * GET /api/front/procurements?customerName=ООО&minPrice=100000&maxPrice=1000000&page=1&size=20
     * GET /api/front/procurements?sortBy=createdAt&sortDirection=ASC
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProcurements(
        @ModelAttribute request: ProcurementListRequest,
        @AuthenticationPrincipal jwtUser: JwtUser
    ): ResponseEntity<ProcurementListFrontResponse> {
        val user = userService.findById(jwtUser.getId())
        val response = procurementFrontService.getProcurements(request, user)
        return ResponseEntity.ok(response)
    }
}
