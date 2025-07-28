package ru.bjcreslin.naidizakupku.news.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.repository.NewsRepository

@Service
class NewsService(val newsRepository: NewsRepository) {

    @Cacheable("getTop10ByOrderByPublicationDateDescCache")
    fun getTopNews(): List<News> {
        return newsRepository.findTop10ByOrderByPublicationDateDesc()
    }
}