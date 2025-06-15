package ru.bjcreslin.naidizakupku.codeService.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.CodeConfiguration
import ru.bjcreslin.naidizakupku.codeService.repository.TelegramCodeRepository
import ru.bjcreslin.naidizakupku.codeService.service.DeleteOldCodes
import ru.bjcreslin.naidizakupku.common.DateTimeUtils
import java.time.LocalDateTime

@Service
class DeleteOldCodesImpl(
    private val telegramCodeRepository: TelegramCodeRepository,
    private val codeConfiguration: CodeConfiguration
) : DeleteOldCodes {
    private val logger: Logger = LoggerFactory.getLogger(DeleteOldCodesImpl::class.java)

    override fun delete() {
        val minTime = getMinimumTime()
        val count = telegramCodeRepository.deleteAllByMaxActionTimeBefore(minTime)
        if (count > 0) {
            logger.info("Deleted $count old codes")
        }
    }

    private fun getMinimumTime(): LocalDateTime =
        DateTimeUtils.getCurrentDateTime().minusMinutes(codeConfiguration.maxAttemptMinutes)
}