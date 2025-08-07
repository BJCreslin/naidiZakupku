package ru.bjcreslin.naidizakupku.cfg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MessageConverterConfiguration : WebMvcConfigurer {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            registerModule(JavaTimeModule())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }

    @Bean
    fun xmlMapper(): XmlMapper {
        return XmlMapper().apply {
            registerModule(JavaTimeModule())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // Add JSON converter first (higher priority) - this makes JSON the default
        val jsonConverter = MappingJackson2HttpMessageConverter(objectMapper())
        jsonConverter.supportedMediaTypes = listOf(
            MediaType.APPLICATION_JSON,
            MediaType("application", "*+json"),
            MediaType.ALL // This ensures JSON is used when no specific content type is requested
        )
        converters.add(jsonConverter)

        // Add XML converter second (lower priority)
        val xmlConverter = MappingJackson2XmlHttpMessageConverter(xmlMapper())
        xmlConverter.supportedMediaTypes = listOf(
            MediaType.APPLICATION_XML,
            MediaType.TEXT_XML,
            MediaType("application", "*+xml")
        )
        converters.add(xmlConverter)
    }
}
