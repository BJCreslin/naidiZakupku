package ru.bjcreslin.naidizakupku.common

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "created_by", nullable = false, updatable = false)
    var createdBy: String? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null,

    @Column(name = "comment", nullable = true, updatable = true, length = 255)
    var comment: String? = null
) {
    @PrePersist
    fun onPrePersist() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        createdBy = SecurityUtils.getCurrentUsername()
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = LocalDateTime.now()
        updatedBy = SecurityUtils.getCurrentUsername()
    }
}
