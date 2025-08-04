package ru.bjcreslin.naidizakupku.news.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import ru.bjcreslin.naidizakupku.news.dbo.News
import ru.bjcreslin.naidizakupku.news.dto.NewsResponseDto

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface NewsMapper {
    
    fun toDto(news: News): NewsResponseDto
    
    fun toDtoList(newsList: List<News>): List<NewsResponseDto>
}