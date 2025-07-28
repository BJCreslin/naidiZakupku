package ru.bjcreslin.naidizakupku.news.parsers

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.common.DateTimeUtils
import ru.bjcreslin.naidizakupku.news.dto.NewsItem
import ru.bjcreslin.naidizakupku.news.dto.Rss
import java.time.LocalDate

@Service
class RssService(
    private val xmlMapper: XmlMapper,
    private val restTemplateBuilder: RestTemplateBuilder
) {

    private val baseUrl = "https://zakupki.gov.ru"
    private val zakupkiGovRuNewsRSS = "/epz/main/public/news/rss?searchString=&newsDateFrom=%s&newsDateTo=&categoryId=3"

    fun loadNews(dateFrom: LocalDate): List<NewsItem> {
        val rssUrl = baseUrl + zakupkiGovRuNewsRSS.format(DateTimeUtils.getLocalDateAsString(dateFrom))
        val restTemplate = restTemplateBuilder.build()
        val xmlResponse = restTemplate.getForObject(rssUrl, String::class.java)

        val rss = xmlMapper.readValue(xmlResponse, Rss::class.java)
        return rss.channel?.items.orEmpty()
    }
}