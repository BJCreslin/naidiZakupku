package ru.bjcreslin.naidizakupku.news.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.news.dbo.News

@Repository
interface NewsRepository : JpaRepository<News, Long> {

    fun findTop10ByOrderByPublicationDateDesc(): List<News>

}