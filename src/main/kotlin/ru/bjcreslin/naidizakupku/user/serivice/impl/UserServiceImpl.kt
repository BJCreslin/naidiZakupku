package ru.bjcreslin.naidizakupku.user.serivice.impl

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.repository.UserRepository
import ru.bjcreslin.naidizakupku.security.service.RoleService
import ru.bjcreslin.naidizakupku.user.serivice.UserService
import ru.bjcreslin.naidizakupku.user.entity.User

@Service
class UserServiceImpl(val userRepository: UserRepository, val roleService: RoleService) : UserService {

    override fun saveNewDeafultUser(user: User): User {
        val encoder = BCryptPasswordEncoder()
        val encodedPassword = encoder.encode(user.password)
        val role = roleService.getDefaultRole()
        return userRepository.save(user.copy(password = encodedPassword, roles = listOf(role)))
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}