package ru.bjcreslin.naidizakupku.common.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.bjcreslin.naidizakupku.common.SecurityUtils
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at")
    open var updatedAt: LocalDateTime? = null,

    @Column(name = "created_by", nullable = false, updatable = false)
    open var createdBy: String? = null,

    @Column(name = "updated_by")
    open var updatedBy: String? = null,

    @Column(name = "comment", nullable = true, updatable = true, length = 255)
    open var comment: String? = null
) {
    @PrePersist
    open fun onPrePersist() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        createdBy = SecurityUtils.getCurrentUsername()
    }

    @PreUpdate
    open fun onPreUpdate() {
        updatedAt = LocalDateTime.now()
        updatedBy = SecurityUtils.getCurrentUsername()
    }
}
