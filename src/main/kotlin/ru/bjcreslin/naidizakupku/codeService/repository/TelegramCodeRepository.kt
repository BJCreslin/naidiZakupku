package ru.bjcreslin.naidizakupku.codeService.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.bjcreslin.naidizakupku.codeService.entity.TelegramCodeEntity
import java.time.LocalDateTime

@Repository
interface TelegramCodeRepository : JpaRepository<TelegramCodeEntity, Long> {

    fun existsByUserId(userId: Long): Boolean

    fun findByUserId(userId: Long): TelegramCodeEntity?

    fun existsByCode(code: Int): Boolean

    fun findByCode(code: Int): TelegramCodeEntity?

    fun deleteAllByMaxActionTimeBefore(time: LocalDateTime): Long

}