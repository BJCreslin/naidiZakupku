package ru.bjcreslin.naidizakupku.news.dbo

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "news")
class News(
    @Column(name = "title", nullable = false, length = 1000)
    var title: String?,

    @Column(name = "url", nullable = false, unique = true)
    var url: String,

    @Column(name = "publication_date")
    var publicationDate: LocalDateTime?,

    @Column(name = "content", columnDefinition = "TEXT")
    var content: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "news_type")
    var newsType: NewsType = NewsType.GENERAL
) : BaseEntity()