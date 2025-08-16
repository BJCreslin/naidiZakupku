package ru.bjcreslin.naidizakupku.telegram.state.entity

import jakarta.persistence.*

@Entity
@Table(name = "telegram_section_user")
open class TelegramSectionUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long = 0,

    @Column(name = "telegram_id", nullable = false)
    open var telegramId: Long,

    /*** Секция телеграмм */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    open var sectionState: SectionState = SectionState.ROOT,

    /*** Дополнительная информация */
    @Column(name = "other_data")
    open var otherData: String? = null

)
