package ru.bjcreslin.naidizakupku.front_api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.front_api.dto.ProjectInfoDto
import ru.bjcreslin.naidizakupku.front_api.service.CommonFrontService

@RestController
@RequestMapping("admin/common")
class CommonFrontController(val commonFrontService: CommonFrontService) {

    @GetMapping("info")
    fun getInfo(): ProjectInfoDto {
        return commonFrontService.getProjectInfo()
    }
}