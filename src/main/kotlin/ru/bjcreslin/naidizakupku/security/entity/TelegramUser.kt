package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User

@Entity
@Table(name = "telegram_users")
class TelegramUser(
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "telegram_id", nullable = true)
    val telegramId: Long?
) : BaseEntity()