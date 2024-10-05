package ru.bjcreslin.naidizakupku.security.service.impl

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.bjcreslin.naidizakupku.security.dto.JwtUser
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.user.repository.UserRepository

@Component
class JwtUserDetailService(
    val repository: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw IllegalArgumentException("Invalid username")
        }

        val user = repository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        return build(user)
    }

    fun build(user: User): UserDetails {
        val authorities: MutableList<SimpleGrantedAuthority>? =
            user.userRoles.stream()
                .map { role -> SimpleGrantedAuthority(role.role.name) }
                .toList()
        return JwtUser(
            user.id,
            user.username,
            user.password,
            user.telegramUser?.telegramId,
            user.enabled,
            authorities
        )
    }
}