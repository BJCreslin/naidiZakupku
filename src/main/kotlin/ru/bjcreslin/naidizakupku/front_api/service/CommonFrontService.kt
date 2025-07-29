package ru.bjcreslin.naidizakupku.front_api.service

import ru.bjcreslin.naidizakupku.front_api.dto.ProjectInfoDto

interface CommonFrontService {

    fun getProjectInfo(): ProjectInfoDto

}