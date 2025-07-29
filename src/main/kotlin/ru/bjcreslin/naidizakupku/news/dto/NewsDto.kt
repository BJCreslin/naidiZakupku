package ru.bjcreslin.naidizakupku.news.dto

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
    var channel: Channel? = null
)

data class Channel(
    @JacksonXmlProperty(localName = "title")
    var title: String? = null,

    @JacksonXmlProperty(localName = "link")
    var link: String? = null,

    @JacksonXmlProperty(localName = "description")
    var description: String? = null,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    var items: List<NewsItem> = emptyList()
)

data class NewsItem(
    @JacksonXmlProperty(localName = "title")
    var title: String? = null,

    @JacksonXmlProperty(localName = "link")
    var link: String? = null,

    @JacksonXmlProperty(localName = "description")
    var description: String? = null,

    @JacksonXmlProperty(localName = "pubDate")
    var pubDate: String? = null
) {
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