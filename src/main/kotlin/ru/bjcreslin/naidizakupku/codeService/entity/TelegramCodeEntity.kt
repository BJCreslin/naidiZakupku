package ru.bjcreslin.naidizakupku.codeService.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "telegram_code",  uniqueConstraints = [
    UniqueConstraint(name = "uc_telegram_code_user_id", columnNames = ["user_id"]),
    UniqueConstraint(name = "uc_telegram_code_code", columnNames = ["code"])
])
class TelegramCodeEntity(

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "code", nullable = false)
    val code: Int,

    @Column(name = "max_action_time", nullable = false)
    val maxActionTime:LocalDateTime
    ): BaseEntity()
