package ru.bjcreslin.naidizakupku.news.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.bjcreslin.naidizakupku.news.dto.NewsResponseDto
import ru.bjcreslin.naidizakupku.news.mapper.NewsMapper
import ru.bjcreslin.naidizakupku.news.service.NewsService

@RestController
@RequestMapping("api/news")
class NewsController(
    val newsService: NewsService,
    val newsMapper: NewsMapper
) {

    @GetMapping("top", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNews(): List<NewsResponseDto> {
        return newsMapper.toDtoList(newsService.getTopNews())
    }
}