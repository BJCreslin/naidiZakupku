package ru.bjcreslin.naidizakupku.chromeExtension.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.ChromeExtensionService
import ru.bjcreslin.naidizakupku.procutement.service.ProcurementService
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.user.serivice.UserService

@Service
class ChromeExtensionServiceImpl(
    private val procurementService: ProcurementService,
    private val userService: UserService
) : ChromeExtensionService {

    private val logger = LoggerFactory.getLogger(ChromeExtensionServiceImpl::class.java)

    override fun saveProcurementToUser(procurementDto: ProcurementDto, jwtUser: JwtUser): String {
        procurementService.saveProcurement(procurementDto, userService.findUserByUserDetails(jwtUser)!!)
        logger.info(procurementDto.toString())
        return procurementDto.registryNumber
    }
}