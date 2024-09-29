package ru.bjcreslin.naidizakupku.user.serivice.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.repository.UserRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService
import ru.bjcreslin.naidizakupku.user.dto.UserDto
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.serivice.UserService

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val roleService: RoleService
) : UserService {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun saveNewDefaultUser(user: User): User {
        val encoder = BCryptPasswordEncoder()
        val encodedPassword = encoder.encode(user.password)
        val role = roleService.getDefaultRole()
        return userRepository.save(user.copy(password = encodedPassword, roles = listOf(role)))
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun createUser(userDto: UserDto): User {
        val role = roleService.getDefaultRole()
        val user = User(
            username = userDto.username,
            enabled = true,
            password = null,
            roles = listOf(role),
            telegramUser = null
        )
        logger.info("Creating user $userDto")
        return userRepository.save(user)
    }
}