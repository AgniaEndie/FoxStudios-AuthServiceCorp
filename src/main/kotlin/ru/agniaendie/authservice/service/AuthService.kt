package ru.agniaendie.authservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.agniaendie.authservice.model.Role
import ru.agniaendie.authservice.model.User
import ru.agniaendie.authservice.model.requests.AuthModel
import ru.agniaendie.authservice.model.requests.RegistryModel
import ru.agniaendie.authservice.model.responses.AuthResponse
import ru.agniaendie.authservice.repository.IUsersRepository
import ru.agniaendie.authservice.security.jwt.JwtTokenService
import java.util.*

@Service
class AuthService(var repository: IUsersRepository, @Value("\${jwt.secret}") secret: String?) {
    val jwtService: JwtTokenService = JwtTokenService(secret)
    suspend fun test(): String {
        return "test"
    }

    suspend fun registry(request: RegistryModel): Any {
        val user = User(
            UUID.randomUUID().toString(),
            request.username,
            request.password,
            request.firstname,
            request.lastname,
            Role.USER
        )
        return repository.save(user)
    }

    suspend fun auth(request: AuthModel): Any {
        return try {
            val user = repository.findUserByUsername(request.username)
            if (user.password == request.password) {
                val token = jwtService.generateToken(user)
                return AuthResponse(user.username, token, user.uuid, user.role)

            } else {
                return ""
            }
        } catch (e: Exception) {
            e.message.toString()
        }

    }


}
