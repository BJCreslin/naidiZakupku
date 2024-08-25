package ru.bjcreslin.naidizakupku.chromeExtension.service

import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto

interface ChromeExtensionService {

    fun saveNewProcurement(procurement: ProcurementDto): String
}