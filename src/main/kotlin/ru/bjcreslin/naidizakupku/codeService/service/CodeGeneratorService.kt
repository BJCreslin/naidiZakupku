package ru.bjcreslin.naidizakupku.codeService.service

import ru.bjcreslin.naidizakupku.codeService.entity.TelegramCodeEntity
import ru.bjcreslin.naidizakupku.user.entity.User

interface CodeGeneratorService {

    fun getNewCode(user: User): TelegramCodeEntity

}