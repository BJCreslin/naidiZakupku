package ru.bjcreslin.naidizakupku.security.service.impl

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.security.entity.User
import ru.bjcreslin.naidizakupku.security.repository.UserRepository
import ru.bjcreslin.naidizakupku.security.service.UserService

@Service
class UserServiceImpl(val userRepository: UserRepository) : UserService {
    override fun saveUser(user: User): User {
        val encoder = BCryptPasswordEncoder()
        val encodedPassword = encoder.encode(user.password)
        return userRepository.save(user.copy(password = encodedPassword))
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}