package ru.bjcreslin.naidizakupku.cfg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.bjcreslin.naidizakupku.security.service.JwtTokenFilter
import ru.bjcreslin.naidizakupku.security.ratelimiter.AuthRateLimitingFilter
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import ru.bjcreslin.naidizakupku.cfg.JwtPropertiesConfiguration


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    @Lazy private val jwtTokenFilter: JwtTokenFilter,
    private val authRateLimitingFilter: AuthRateLimitingFilter
) {

    @Bean
    fun authenticationManagerBean(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http

            .cors(Customizer.withDefaults())
            .csrf { it.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/health", "/api/health/**",
                        "/api/v1/login/**", "/api/v1/verify-token/**",
                        "/api/admin/login/**", "/api/admin/login",
                        "/api/news/**", "/admin/common/**",
                        "/api/auth/telegram/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }

            .httpBasic(Customizer.withDefaults())

            // ✅ Stateless для всех, кроме H2 (она исключена через webSecurityCustomizer)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(authRateLimitingFilter, JwtTokenFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:5173",
            "http://localhost:3000",
            "https://naidizakupku.ru",
            "https://web.telegram.org"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
