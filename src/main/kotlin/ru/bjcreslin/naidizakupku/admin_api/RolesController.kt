package ru.bjcreslin.naidizakupku.admin_api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.admin_api.dto.RoleDto
import ru.bjcreslin.naidizakupku.admin_api.service.RolesService

@RestController
@RequestMapping("/api/admin/v1")
class RolesController(val rolesService: RolesService) {

    @GetMapping("/roles")
    fun getAll(): List<RoleDto> {
        return rolesService.getAll()
    }
//    GET    /roles               -> список всех ролей
//    POST   /roles               -> создать новую роль
//    PUT    /roles/{id}          -> обновить
//    DELETE /roles/{id}          -> удалить
//
//    GET    /users/{id}/roles    -> роли конкретного пользователя
//    POST   /users/{id}/roles    -> назначить пользователю роли
}