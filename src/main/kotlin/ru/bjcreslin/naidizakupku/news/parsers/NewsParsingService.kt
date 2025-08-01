package ru.bjcreslin.naidizakupku.news.parsers

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.dbo.NewsType
import ru.bjcreslin.naidizakupku.news.repository.NewsRepository
import java.time.LocalDate

@Service
class NewsParsingService(val newsRepository: NewsRepository, val rssService: RssService) {

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

    private fun saveNewNews(newsItems: List<News>): Int {
        return newsRepository.saveAll(newsItems).size
    }
}