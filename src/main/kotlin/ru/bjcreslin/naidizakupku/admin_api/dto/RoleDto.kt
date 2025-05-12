package ru.bjcreslin.naidizakupku.admin_api.dto

import java.time.LocalDateTime

data class RoleDto(

    var id: Int,

    val name: String,

    var createdAt: LocalDateTime? = null,

    var updatedAt: LocalDateTime? = null,

    var createdBy: String? = null,

    var updatedBy: String? = null,

    var comment: String? = null
)