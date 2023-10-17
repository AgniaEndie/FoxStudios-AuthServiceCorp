package ru.agniaendie.authservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.agniaendie.authservice.repository.IUsersRepository

@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired var repository: IUsersRepository,@Value("\${jwt.secret}") secret : String?) {
    @Value("\${jwt.secret}")
    private var secret: String? = secret
    val jwtAuthFilter by lazy {
        ru.agniaendie.authservice.security.filter.UsernamePasswordAuthenticationFilter(
            repository, secret
        )
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }.cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "main/test1",
                    "main/registry",
                    "main/auth"
                ).permitAll().anyRequest().authenticated()
            }
            .sessionManagement { sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .anonymous { anonymous -> anonymous.disable() }
            .addFilterBefore(
                jwtAuthFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint { request, response, authException ->
                        System.err.println("Failed! $authException \n $request \n $response")
                        response.status = 401
                    }
                    .accessDeniedHandler(AccessDeniedHandlerImpl())
            }.securityContext { securityContext -> securityContext.requireExplicitSave(false) }.build()
    }
    @Bean
    suspend fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues())
        config.exposedHeaders = listOf("Authorization")
        return source
    }
}



