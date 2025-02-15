package ru.bjcreslin.naidizakupku.telegramUser.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.UsersConfiguration
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.security.repository.UserRoleRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser
import ru.bjcreslin.naidizakupku.telegramUser.repository.TelegramUserRepository
import ru.bjcreslin.naidizakupku.user.dto.UserDto
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.serivice.UserService

@Service
class TelegramUserServiceImpl(
    private val telegramUserRepository: TelegramUserRepository,
    private val userService: UserService,
    private val usersConfiguration: UsersConfiguration
) : TelegramUserService {

    private val logger: Logger = LoggerFactory.getLogger(TelegramUserServiceImpl::class.java)

    override fun getNewOrSavedUserByTelegramId(telegramId: Long): User {
        var telegramUser = telegramUserRepository.findByTelegramId(telegramId)
        val user: User
        if (telegramUser == null) {
            val userDto = UserDto("${usersConfiguration.telegramUserNamePrefix}$telegramId")
            user = userService.createUser(userDto)
            telegramUser = TelegramUser(user = user, telegramId = telegramId)
            telegramUserRepository.save(telegramUser)
            logger.info("User with telegramId $telegramId created")
        }
        return telegramUser.user
    }
}