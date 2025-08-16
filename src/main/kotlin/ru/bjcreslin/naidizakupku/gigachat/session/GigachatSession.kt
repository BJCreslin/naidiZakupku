package ru.bjcreslin.naidizakupku.gigachat.session


import jakarta.persistence.*

@Entity
@Table(
    name = "gigachat_session",
    indexes = [Index(name = "idx_telegram_id", columnList = "telegram_id", unique = false)]
)
open class GigachatSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long = 0,

    @Column(name = "telegram_id", nullable = false)
    open var telegramId: Long = 0,

    @Column(name = "session_id")
    open var sessionId: String = ""
)