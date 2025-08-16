package ru.bjcreslin.naidizakupku.healthCheck

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ThreadMXBean
import java.time.LocalDateTime
import jakarta.persistence.EntityManagerFactory

@RestController
@RequestMapping("/api/health")
class HealthCheckController(
    private val meterRegistry: MeterRegistry,
    private val customMetricsService: CustomMetricsService
) {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @GetMapping
    fun health(): ResponseEntity<ServerStatusResponse> {
        val entityManagerStatus = checkEntityManagerStatus()
        
        val status = ServerStatusResponse(
            status = if (entityManagerStatus.isOpen) "UP" else "DOWN",
            timestamp = LocalDateTime.now(),
            uptime = getUptime(),
            memory = getMemoryInfo(),
            threads = getThreadInfo(),
            metrics = getBasicMetrics(),
            databaseStatus = entityManagerStatus
        )
        
        return ResponseEntity.ok(status)
    }

    @GetMapping("/detailed")
    fun detailedHealth(): ResponseEntity<DetailedServerStatus> {
        val memoryBean = ManagementFactory.getMemoryMXBean()
        val threadBean = ManagementFactory.getThreadMXBean()
        val entityManagerStatus = checkEntityManagerStatus()
        
        val detailedStatus = DetailedServerStatus(
            status = if (entityManagerStatus.isOpen) "UP" else "DOWN",
            timestamp = LocalDateTime.now(),
            uptime = getUptime(),
            memory = DetailedMemoryInfo(
                heapUsed = memoryBean.heapMemoryUsage.used,
                heapMax = memoryBean.heapMemoryUsage.max,
                nonHeapUsed = memoryBean.nonHeapMemoryUsage.used,
                nonHeapMax = memoryBean.nonHeapMemoryUsage.max
            ),
            threads = DetailedThreadInfo(
                total = threadBean.threadCount,
                daemon = threadBean.daemonThreadCount,
                peak = threadBean.peakThreadCount
            ),
            system = SystemInfo(
                processors = Runtime.getRuntime().availableProcessors(),
                totalMemory = Runtime.getRuntime().totalMemory(),
                freeMemory = Runtime.getRuntime().freeMemory(),
                maxMemory = Runtime.getRuntime().maxMemory()
            ),
            metrics = getDetailedMetrics(),
            databaseStatus = entityManagerStatus
        )
        
        return ResponseEntity.ok(detailedStatus)
    }

    private fun checkEntityManagerStatus(): DatabaseStatus {
        return try {
            val entityManagerFactory = applicationContext.getBean(EntityManagerFactory::class.java)
            DatabaseStatus(
                isOpen = entityManagerFactory.isOpen,
                status = if (entityManagerFactory.isOpen) "CONNECTED" else "CLOSED",
                message = if (entityManagerFactory.isOpen) "EntityManagerFactory is open and ready" else "EntityManagerFactory is closed"
            )
        } catch (e: Exception) {
            DatabaseStatus(
                isOpen = false,
                status = "ERROR",
                message = "Failed to check EntityManagerFactory status: ${e.message}"
            )
        }
    }

    private fun getUptime(): String {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val hours = uptime / (1000 * 60 * 60)
        val minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (uptime % (1000 * 60)) / 1000
        return "${hours}h ${minutes}m ${seconds}s"
    }

    private fun getMemoryInfo(): String {
        val memoryBean = ManagementFactory.getMemoryMXBean()
        val heapUsed = memoryBean.heapMemoryUsage.used / (1024 * 1024)
        val heapMax = memoryBean.heapMemoryUsage.max / (1024 * 1024)
        return "${heapUsed}MB / ${heapMax}MB"
    }

    private fun getThreadInfo(): String {
        val threadBean = ManagementFactory.getThreadMXBean()
        return "${threadBean.threadCount} active"
    }

    private fun getBasicMetrics(): Map<String, Any> {
        return mapOf(
            "jvm_memory_used" to (meterRegistry.get("jvm.memory.used").gauge()?.value() ?: 0.0),
            "jvm_memory_max" to (meterRegistry.get("jvm.memory.max").gauge()?.value() ?: 0.0),
            "jvm_threads_live" to (meterRegistry.get("jvm.threads.live").gauge()?.value() ?: 0.0),
            "process_cpu_usage" to (meterRegistry.get("process.cpu.usage").gauge()?.value() ?: 0.0)
        )
    }

    private fun getDetailedMetrics(): Map<String, Any> {
        return mapOf(
            "jvm_memory_used" to (meterRegistry.get("jvm.memory.used").gauge()?.value() ?: 0.0),
            "jvm_memory_max" to (meterRegistry.get("jvm.memory.max").gauge()?.value() ?: 0.0),
            "jvm_threads_live" to (meterRegistry.get("jvm.threads.live").gauge()?.value() ?: 0.0),
            "jvm_threads_peak" to (meterRegistry.get("jvm.threads.peak").gauge()?.value() ?: 0.0),
            "process_cpu_usage" to (meterRegistry.get("process.cpu.usage").gauge()?.value() ?: 0.0),
            "system_cpu_usage" to (meterRegistry.get("system.cpu.usage").gauge()?.value() ?: 0.0),
            "http_server_requests_total" to (meterRegistry.get("http.server.requests").counter()?.count() ?: 0.0),
            "cache_size_total" to (meterRegistry.get("cache.size").gauge()?.value() ?: 0.0)
        )
    }
}

data class ServerStatusResponse(
    val status: String,
    val timestamp: LocalDateTime,
    val uptime: String,
    val memory: String,
    val threads: String,
    val metrics: Map<String, Any>,
    val databaseStatus: DatabaseStatus
)

data class DetailedServerStatus(
    val status: String,
    val timestamp: LocalDateTime,
    val uptime: String,
    val memory: DetailedMemoryInfo,
    val threads: DetailedThreadInfo,
    val system: SystemInfo,
    val metrics: Map<String, Any>,
    val databaseStatus: DatabaseStatus
)

data class DatabaseStatus(
    val isOpen: Boolean,
    val status: String,
    val message: String
)

data class DetailedMemoryInfo(
    val heapUsed: Long,
    val heapMax: Long,
    val nonHeapUsed: Long,
    val nonHeapMax: Long
)

data class DetailedThreadInfo(
    val total: Int,
    val daemon: Int,
    val peak: Int
)

data class SystemInfo(
    val processors: Int,
    val totalMemory: Long,
    val freeMemory: Long,
    val maxMemory: Long
)