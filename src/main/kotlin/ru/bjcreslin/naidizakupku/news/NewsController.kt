package ru.bjcreslin.naidizakupku.news

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.repository.NewsRepository

@RestController
@RequestMapping("api/news")
class NewsController(val repository: NewsRepository) {

    @GetMapping
    fun getNews(): List<News> {
        return repository.findTop10ByOrderByPublicationDateDesc()
    }
}