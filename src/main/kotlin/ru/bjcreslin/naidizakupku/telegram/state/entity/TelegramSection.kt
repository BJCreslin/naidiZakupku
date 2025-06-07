package ru.bjcreslin.naidizakupku.telegram.state.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity

@Entity
@Table(name = "telegram_section_user")
class TelegramSectionUser(

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long,

    /*** Сеуция телеграмм */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    var sectionState: SectionState = SectionState.ROOT,

    /*** Дополнительная информация */
    @Column(name = "other_data")
    var otherData: String? = null

) : BaseEntity()
