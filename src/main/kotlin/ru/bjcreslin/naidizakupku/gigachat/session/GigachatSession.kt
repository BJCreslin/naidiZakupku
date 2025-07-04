package ru.bjcreslin.naidizakupku.gigachat.session


import jakarta.persistence.*

@Entity
@Table(
    name = "gigachat_session",
    indexes = [Index(name = "idx_telegram_id", columnList = "telegram_id", unique = false)]
)
class GigachatSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "telegram_id", nullable = false)
    val telegramId: Long,

    @Column(name = "session_id")
    val sessionId: String
)