package ru.bjcreslin.naidizakupku.cfg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setConnectTimeout(5000)
        return RestTemplate(factory)
    }
}