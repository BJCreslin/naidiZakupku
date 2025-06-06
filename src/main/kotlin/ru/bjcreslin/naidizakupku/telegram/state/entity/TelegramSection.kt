package ru.bjcreslin.naidizakupku.telegram.state.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User

@Entity
@Table(name = "telegram_section_user")
class TelegramSectionUser(

    /*** Пользователь */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    var user: User,

    /*** Сеуция телеграмм */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    var sectionState: SectionState = SectionState.MAIN_MENU,

    /*** Дополнительная информация */
    @Column(name = "other_data")
    var otherData: String? = null

) : BaseEntity()
