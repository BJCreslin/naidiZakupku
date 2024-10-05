package ru.bjcreslin.naidizakupku.chromeExtension.service

import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.security.dto.JwtUser

interface ChromeExtensionService {

    /**
     * Добавление закупки пользователю
     */
    @Transactional
    fun saveProcurementToUser(procurementDto: ProcurementDto, jwtUser: JwtUser): String
}