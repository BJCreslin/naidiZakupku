package ru.bjcreslin.naidizakupku.news.parsers

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.cfg.CustomMetricsService
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.dbo.NewsType
import ru.bjcreslin.naidizakupku.news.repository.NewsRepository
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class NewsParsingService(
    private val rssService: RssService,
    private val newsRepository: NewsRepository,
    private val customMetricsService: CustomMetricsService
) {

    private val logger = LoggerFactory.getLogger(NewsParsingService::class.java)

    private val baseUrl = "https://zakupki.gov.ru"

    @Scheduled(fixedRate = 3_600_000) // Каждый час
    @Transactional
    fun parseNews() {
        try {
            logger.info("Starting news parsing...")

            val newsItems = rssService.loadNews(LocalDate.now().minusDays(20))
                .map { newsItem ->
                    News(
                        title = newsItem.title, url = baseUrl + newsItem.link, publicationDate = newsItem.publishedAt,
                        content = newsItem.description,
                        newsType = NewsType.GENERAL
                    )
                }
            val savedNewsItems = newsRepository.findAll()

            val newNewsItems =
                newsItems.filter { newsItem -> savedNewsItems.none { saved -> saved.url == newsItem.url } }.toList()

            val savedCount = saveNewNews(newNewsItems)

            logger.info("Parsing completed. Saved $savedCount new items")
        } catch (e: Exception) {
            logger.error("Error during news parsing", e)
        }
    }

    fun parseAndSaveNews() {
        val sources = listOf(
            Pair("https://zakupki.gov.ru/epz/main/public/home.html", NewsType.PROCUREMENT),
            Pair("https://www.goszakupki.ru/news/", NewsType.PROCUREMENT),
            Pair("https://zakupki.rosatom.ru/news/", NewsType.ATOMIC)
        )

        sources.forEach { (url, type) ->
            val startTime = System.currentTimeMillis()
            
            try {
                val newsItems = rssService.parseRssFeed(url)
                val savedCount = saveNewsItems(newsItems, type)
                
                val parsingTime = System.currentTimeMillis() - startTime
                customMetricsService.recordNewsParsingTime(url, parsingTime)
                
                logger.info("Parsed $savedCount news items from $url in ${parsingTime}ms")
                
            } catch (e: Exception) {
                val parsingTime = System.currentTimeMillis() - startTime
                customMetricsService.recordNewsParsingTime(url, parsingTime)
                logger.error("Error parsing news from $url", e)
            }
        }
    }

    private fun saveNewNews(newsItems: List<News>): Int {
        return newsRepository.saveAll(newsItems).size
    }

    private fun saveNewsItems(newsItems: List<News>, type: NewsType): Int {
        var savedCount = 0
        
        newsItems.forEach { news ->
            try {
                news.newsType = type
                news.createdAt = LocalDateTime.now()
                
                // Проверяем, не существует ли уже такая новость
                val existingNews = newsRepository.findByTitleAndUrl(news.title ?: "", news.url)
                if (existingNews == null) {
                    newsRepository.save(news)
                    savedCount++
                }
            } catch (e: Exception) {
                logger.error("Error saving news: ${news.title}", e)
            }
        }
        
        return savedCount
    }
}