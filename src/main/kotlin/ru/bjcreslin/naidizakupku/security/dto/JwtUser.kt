package ru.bjcreslin.naidizakupku.security.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUser(
    private val id: Long,
    private val username: String,
    @JsonIgnore
    private val password: String?,
    private val telegramUserId: Long?,
    private val enabled: Boolean,
    private val authorities: MutableList<SimpleGrantedAuthority>?
) : UserDetails {

    override fun getAuthorities(): MutableList<SimpleGrantedAuthority>? {
        return authorities
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}