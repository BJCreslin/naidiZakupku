package ru.bjcreslin.naidizakupku.front_api.service

import org.jvnet.hk2.annotations.Service
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.front_api.dto.ProjectInfoDto
import ru.bjcreslin.naidizakupku.procurement.repository.ProcurementRepository

@Service
open class CommonFrontServiceImpl(val procurementRepository: ProcurementRepository) : CommonFrontService {

    @Transactional
    @Cacheable("ProjectInfoCache")
    override fun getProjectInfo(): ProjectInfoDto {
        return procurementRepository.getProjectInfo()
    }
}