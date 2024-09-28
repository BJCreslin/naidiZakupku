package ru.bjcreslin.naidizakupku.codeService.service.impl

import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.CodeConfiguration
import ru.bjcreslin.naidizakupku.codeService.service.CodeGeneratorService
import ru.bjcreslin.naidizakupku.codeService.entity.TelegramCodeEntity
import ru.bjcreslin.naidizakupku.codeService.repository.TelegramCodeRepository
import ru.bjcreslin.naidizakupku.user.entity.User
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class CodeGeneratorServiceImpl(
    private val telegramCodeRepository: TelegramCodeRepository,
    private val codeConfiguration: CodeConfiguration
) : CodeGeneratorService {

    override fun getNewCode(user: User): TelegramCodeEntity {
        val code = generateUniqueCode()
        val maxActionTime = LocalDateTime.now().plusMinutes(codeConfiguration.maxAttemptMinutes)
        val telegramCodeEntity = TelegramCodeEntity(user = user, code = code, maxActionTime = maxActionTime)
        return telegramCodeRepository.save(telegramCodeEntity)
    }

    private fun generateUniqueCode(): Int {
        var code: Int

        do {
            code = generateCode()
        } while (telegramCodeRepository.existsByCode(code))
        return code
    }

    private fun generateCode(): Int {
        return Random.nextInt(codeConfiguration.minimum, codeConfiguration.maximum)
    }
}