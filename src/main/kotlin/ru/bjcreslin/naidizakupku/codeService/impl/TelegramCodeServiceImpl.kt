package ru.bjcreslin.naidizakupku.codeService.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.codeService.TelegramCodeService
import ru.bjcreslin.naidizakupku.codeService.dto.TelegramCodeDto
import ru.bjcreslin.naidizakupku.codeService.entity.TelegramCodeEntity
import ru.bjcreslin.naidizakupku.codeService.repository.TelegramCodeRepository
import ru.bjcreslin.naidizakupku.codeService.service.CodeGeneratorService
import ru.bjcreslin.naidizakupku.codeService.service.DeleteOldCodes
import ru.bjcreslin.naidizakupku.user.entity.User

@Service
class TelegramCodeServiceImpl(
    private val codeGeneratorService: CodeGeneratorService,
    private val telegramCodeRepository: TelegramCodeRepository,
    private val deleteOldCodes: DeleteOldCodes
) : TelegramCodeService {
    private val logger: Logger = LoggerFactory.getLogger(TelegramCodeServiceImpl::class.java)

    override fun createCode(user: User): TelegramCodeDto {
        deleteOldCodes.delete()
        val entityCode = getOrCreateCode(user)
        logger.debug("Generated code: {} for user: {}", entityCode.code, user)
        return TelegramCodeDto(code = entityCode.code, maxActionTime = entityCode.maxActionTime)
    }

    override fun getUserByCode(code: Int): User? {
        deleteOldCodes.delete()
        val telegramCodeEntity = telegramCodeRepository.findByCode(code)
        return telegramCodeEntity?.let {
            val user = it.user
            telegramCodeRepository.delete(it)
            user
        }
    }


    private fun getOrCreateCode(user: User): TelegramCodeEntity {
        val existingCode = telegramCodeRepository.findByUserId(user.id)
        return existingCode ?: codeGeneratorService.getNewCode(user)
    }
}