package ru.bjcreslin.naidizakupku.security.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.bjcreslin.naidizakupku.security.entity.User

class JwtUser(
    val id: Long,
    val username: String,
    @JsonIgnore
    val password: String,
    val telegramUserId: Long,
    val enabled: Boolean,
    val authorities: MutableCollection<out GrantedAuthority>
) : UserDetails {

    fun build(user: User): UserDetails {
        val authorities: Unit = user.getRoles().stream()
            .map { role -> SimpleGrantedAuthority(role.getName()) }
            .toList()
        return JwtUser(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getTelegramUserId(),
            user.getPassword(),
            user.isEnabled(),
            authorities
        )
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}