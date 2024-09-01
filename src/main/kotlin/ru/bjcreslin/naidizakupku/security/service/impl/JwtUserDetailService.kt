package ru.bjcreslin.naidizakupku.security.service.impl

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ru.bjcreslin.naidizakupku.security.repository.UserRepository

class JwtUserDetailService(val repository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw IllegalArgumentException("Invalid username")
        }

        val user = repository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        return JwtUser.build(user)
    }
}