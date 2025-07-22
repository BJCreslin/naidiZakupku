package ru.bjcreslin.naidizakupku.healthCheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.common.DateTimeUtils

@RestController
@RequestMapping("api/health")
class HealthCheckController {

    @GetMapping
    fun checkHealth(): HealthResponse {
        return HealthResponse(
            status = ServerStatus.UP,
            timestamp = DateTimeUtils.getCurrentDateAsString()
        )
    }
}

data class HealthResponse(
    val status: ServerStatus,
    val timestamp: String
)