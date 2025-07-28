package ru.bjcreslin.naidizakupku.news.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.service.NewsService

@RestController
@RequestMapping("api/news")
class NewsController(val newsService: NewsService) {

    @GetMapping("top")
    fun getNews(): List<News> {
        return newsService.getTopNews()
    }
}