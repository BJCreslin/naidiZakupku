package ru.bjcreslin.naidizakupku.user.serivice.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.security.entity.UserRole
import ru.bjcreslin.naidizakupku.security.repository.UserRoleRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService
import ru.bjcreslin.naidizakupku.user.dto.UserDto
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.repository.UserRepository
import ru.bjcreslin.naidizakupku.user.serivice.UserService

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    val roleService: RoleService
) : UserService {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun createUser(userDto: UserDto): User {
        val user = User(
            username = userDto.username,
            enabled = true,
            password = null,
            telegramUser = null
        )
        val defaultRole = roleService.getDefaultRole()
        val userRole = UserRole(user = user, role = defaultRole)
        userRepository.save(user)
        userRoleRepository.save(userRole)
        logger.info("Creating user $userDto")
        return user
    }

    override fun findUserByUserDetails(jwtUser: JwtUser): User? {
        return userRepository.findByUsername(jwtUser.username)
    }

}