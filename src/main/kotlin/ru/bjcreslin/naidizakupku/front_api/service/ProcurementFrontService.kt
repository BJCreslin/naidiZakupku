package ru.bjcreslin.naidizakupku.front_api.service

import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListFrontResponse
import ru.bjcreslin.naidizakupku.front_api.dto.ProcurementListRequest
import ru.bjcreslin.naidizakupku.user.entity.User

/**
 * Сервис для работы с закупками на фронте
 */
interface ProcurementFrontService {
    
    /**
     * Получает список закупок пользователя с фильтрацией, сортировкой и пагинацией
     * @param request параметры запроса
     * @param user пользователь
     * @return список закупок с метаданными
     */
    fun getProcurements(request: ProcurementListRequest, user: User): ProcurementListFrontResponse
}
