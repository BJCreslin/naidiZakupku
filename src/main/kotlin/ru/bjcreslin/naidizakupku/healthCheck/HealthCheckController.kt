package ru.bjcreslin.naidizakupku.healthCheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("api/health")
class HealthCheckController {
    @GetMapping
    fun checkHealth(): HealthResponse {
        return HealthResponse(
            status = "UP",
            timestamp = Instant.now().toString()
        )
    }
}

data class HealthResponse(
    val status: String,
    val timestamp: String
)