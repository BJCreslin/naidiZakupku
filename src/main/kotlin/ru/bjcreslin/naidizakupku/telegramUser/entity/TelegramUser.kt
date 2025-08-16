package ru.bjcreslin.naidizakupku.telegramUser.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User

@Entity
@Table(name = "telegram_users")
open class TelegramUser(

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User,

    @Column(name = "telegram_id", nullable = false)
    open var telegramId: Long
) : BaseEntity()