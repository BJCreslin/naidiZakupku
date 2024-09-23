package ru.bjcreslin.naidizakupku.chromeExtension.controller.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.ChromeExtensionService

@RestController
@RequestMapping("api/chromeExtension/v1")
class ChromeExtensionController(private val chromeExtensionService: ChromeExtensionService) {

    @PostMapping("procurement")
    fun newProcurement(@RequestBody procurement: ProcurementDto): ResponseEntity<String> {
        val result = chromeExtensionService.saveNewProcurement(procurement)
        return ResponseEntity(result, HttpStatus.CREATED)
    }
}
