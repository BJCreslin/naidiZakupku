package ru.bjcreslin.naidizakupku.news.dbo

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "news")
open class News(
    @Column(name = "title", nullable = false, length = 1000)
    open var title: String? = null,

    @Column(name = "url", nullable = false, unique = true)
    open var url: String = "",

    @Column(name = "publication_date")
    open var publicationDate: LocalDateTime? = null,

    @Column(name = "content", columnDefinition = "CLOB")
    open var content: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "news_type")
    open var newsType: NewsType = NewsType.GENERAL
) : BaseEntity()