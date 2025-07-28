package ru.bjcreslin.naidizakupku.news

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.time.LocalDateTime
import java.time.ZoneId.of
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@JacksonXmlRootElement(localName = "rss")
data class Rss(
    @JacksonXmlProperty(localName = "channel")
    val channel: Channel? = null
)

data class Channel(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    val items: List<NewsItem> = emptyList()
)

data class NewsItem(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val pubDate: String? = null
)
{
    val publishedAt: LocalDateTime?
        get() = parsePubDateToMoscow(pubDate)
}

fun parsePubDateToMoscow(pubDate: String?): LocalDateTime? {
    if (pubDate.isNullOrBlank()) return null

    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

    return ZonedDateTime
        .parse(pubDate, formatter)                          // Парсим как GMT (из строки)
        .withZoneSameInstant(of("Europe/Moscow"))   // Переводим во временную зону Москвы
        .toLocalDateTime()                                 // Возвращаем LocalDateTime
}