package ru.agniaendie.authservice.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.agniaendie.authservice.model.User
import ru.agniaendie.authservice.repository.IUsersRepository
import ru.agniaendie.authservice.security.jwt.JwtTokenService

@Component
class UsernamePasswordAuthenticationFilter(
    @Autowired var usersRepository: IUsersRepository,
    @Value("\${jwt.secret}") secret: String?
) : OncePerRequestFilter() {
    var jwtTokenService = JwtTokenService(secret)
    val log = LoggerFactory.getLogger(this.javaClass)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        log.error(header)
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            if (jwtTokenService.validateToken(token)) {
                val username: String = jwtTokenService.extractClaim(token) { claims ->
                    claims!!.get(
                        "sub",
                        String::class.java
                    )
                }
                val user: User = usersRepository.findUserByUsername(username)
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    user,
                    user.role,
                    user.authorities
                )
                val ctx = SecurityContextHolder.createEmptyContext()
                SecurityContextHolder.setContext(ctx)
                ctx.authentication = authenticationToken
                if (SecurityContextHolder.getContext().authentication == null) {
                    log.error("Произошла ошибка в модуле авторизации")
                }
            } else {
                log.error("Токен истек$token")
            }
        }

        filterChain.doFilter(request, response)

    }
}
