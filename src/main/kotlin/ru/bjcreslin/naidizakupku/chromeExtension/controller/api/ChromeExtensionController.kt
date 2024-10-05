package ru.bjcreslin.naidizakupku.chromeExtension.controller.api

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.chromeExtension.dto.ProcurementDto
import ru.bjcreslin.naidizakupku.chromeExtension.service.ChromeExtensionService
import ru.bjcreslin.naidizakupku.security.dto.JwtUser

@RestController
@RequestMapping("api/chromeExtension/v1")
class ChromeExtensionController(private val chromeExtensionService: ChromeExtensionService) {

    @PostMapping("procurement")
    fun newProcurement(
        @RequestBody @Valid procurement: ProcurementDto,
        @AuthenticationPrincipal jwtUser: JwtUser
    ): ResponseEntity<String> {
        val result = chromeExtensionService.saveProcurementToUser(procurement, jwtUser)
        return ResponseEntity(result, HttpStatus.CREATED)
    }
}
