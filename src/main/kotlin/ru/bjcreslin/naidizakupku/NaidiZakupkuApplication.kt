package ru.bjcreslin.naidizakupku

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.bjcreslin.naidizakupku.cfg.JwtProperties
import ru.bjcreslin.naidizakupku.cfg.UsersProperties

@SpringBootApplication
@EnableConfigurationProperties(value =[JwtProperties::class,UsersProperties::class])
class NaidiZakupkuApplication

fun main(args: Array<String>) {
    runApplication<NaidiZakupkuApplication>(*args)
}
