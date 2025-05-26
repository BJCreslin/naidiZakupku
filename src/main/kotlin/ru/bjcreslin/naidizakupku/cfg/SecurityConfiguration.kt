package ru.bjcreslin.naidizakupku.cfg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import ru.bjcreslin.naidizakupku.security.service.JwtTokenProvider


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    @Throws(Exception::class)
    fun authenticationManagerBean(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: AbstractHttpConfigurer<*, *> -> obj.disable() }
            .authorizeHttpRequests { authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/api/health", "/api/health/**").permitAll()
                    .requestMatchers("/api/v1/login/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/api/chromeExtension/v1/login**").permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
        return http.build()
    }

    @Bean
    protected fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

}