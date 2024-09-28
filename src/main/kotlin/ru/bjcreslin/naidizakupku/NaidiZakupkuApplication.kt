package ru.bjcreslin.naidizakupku

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.bjcreslin.naidizakupku.cfg.CodeConfiguration
import ru.bjcreslin.naidizakupku.cfg.JwtPropertiesConfiguration
import ru.bjcreslin.naidizakupku.cfg.UsersConfiguration

@SpringBootApplication
@EnableConfigurationProperties(
    value = [JwtPropertiesConfiguration::class, UsersConfiguration::class, CodeConfiguration::class]
)
class NaidiZakupkuApplication

fun main(args: Array<String>) {
    runApplication<NaidiZakupkuApplication>(*args)
}
