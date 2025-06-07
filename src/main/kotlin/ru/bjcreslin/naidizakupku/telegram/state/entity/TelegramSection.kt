package ru.bjcreslin.naidizakupku.telegram.state.entity

import jakarta.persistence.*

@Entity
@Table(name = "telegram_section_user")
class TelegramSectionUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long,

    /*** Сеуция телеграмм */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    var sectionState: SectionState = SectionState.ROOT,

    /*** Дополнительная информация */
    @Column(name = "other_data")
    var otherData: String? = null

)
