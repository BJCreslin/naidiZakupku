package ru.bjcreslin.naidizakupku.cfg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import jakarta.annotation.PreDestroy

@Configuration
@EnableScheduling
class SchedulingConfiguration : SchedulingConfigurer {

    private val executor = Executors.newScheduledThreadPool(5)

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(executor)
    }

    @PreDestroy
    fun shutdown() {
        executor.shutdown()
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow()
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    println("Scheduled executor did not terminate")
                }
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}