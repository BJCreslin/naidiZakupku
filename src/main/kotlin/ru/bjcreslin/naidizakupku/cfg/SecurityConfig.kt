package ru.bjcreslin.naidizakupku.cfg

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import ru.bjcreslin.naidizakupku.user.serivice.UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userService: UserService,
    private val jwtFilter: JwtFilter) : WebSecurityConfigurerAdapter(){


}