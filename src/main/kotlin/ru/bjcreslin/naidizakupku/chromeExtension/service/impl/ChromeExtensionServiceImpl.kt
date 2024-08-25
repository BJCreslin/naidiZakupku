package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.ChromeExtensionService

@Service
class ChromeExtensionServiceImpl : ChromeExtensionService {

    private val logger = LoggerFactory.getLogger(ChromeExtensionServiceImpl::class.java)

    override fun saveNewProcurement(procurement: ProcurementDto): String {
        logger.info(procurement.toString())
        return procurement.registryNumber
    }
}