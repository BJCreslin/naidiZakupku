package ru.bjcreslin.naidizakupku.news.dto

import ru.bjcreslin.naidizakupku.news.dbo.NewsType
import java.time.LocalDateTime

data class NewsResponseDto(
    val id: Long?,
    val title: String?,
    val url: String,
    val publicationDate: LocalDateTime?,
    val content: String?,
    val newsType: NewsType
)