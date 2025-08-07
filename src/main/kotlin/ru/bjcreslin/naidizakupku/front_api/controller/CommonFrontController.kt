package ru.bjcreslin.naidizakupku.front_api.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.front_api.dto.ProjectInfoDto
import ru.bjcreslin.naidizakupku.front_api.service.CommonFrontService

@RestController
@RequestMapping("api/common")
class CommonFrontController(
    private val commonFrontService: CommonFrontService) {

    @GetMapping("info", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getInfo(): ProjectInfoDto {
        return commonFrontService.getProjectInfo()
    }
}