package ru.bjcreslin.naidizakupku.security.entity

import jakarta.persistence.*
import ru.bjcreslin.naidizakupku.common.entity.BaseEntity
import ru.bjcreslin.naidizakupku.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "user_sessions")
open class UserSession(
    @Column(name = "session_id", unique = true, nullable = false)
    open var sessionId: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User = User(),

    @Column(name = "telegram_id", nullable = false)
    open var telegramId: Long = 0,

    @Column(name = "expires_at", nullable = false)
    open var expiresAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_active", nullable = false)
    open var isActive: Boolean = true
) : BaseEntity()
