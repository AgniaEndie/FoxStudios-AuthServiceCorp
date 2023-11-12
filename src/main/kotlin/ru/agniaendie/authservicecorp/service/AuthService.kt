package ru.agniaendie.authservicecorp.service

import org.apache.http.HttpEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.agniaendie.authservicecorp.model.Role
import ru.agniaendie.authservicecorp.model.User
import ru.agniaendie.authservicecorp.model.requests.AuthModel
import ru.agniaendie.authservicecorp.model.requests.RegistryModel
import ru.agniaendie.authservicecorp.model.responses.AuthResponse
import ru.agniaendie.authservicecorp.repository.IUsersRepository
import ru.agniaendie.authservicecorp.security.jwt.JwtTokenService
import java.util.*

@Service
class AuthService(var repository: IUsersRepository, @Value("\${jwt.secret}") secret: String?) {
    val jwtService: JwtTokenService = JwtTokenService(secret)
    suspend fun test(): String {
        return "test"
    }

    suspend fun registry(request: RegistryModel, encoder: PasswordEncoder) {
        val user = User(
            UUID.randomUUID().toString(),
            request.username,
            encoder.encode(request.password),
            request.firstname,
            request.lastname,
            request.surname,
            request.email,
            Role.USER
        )
        repository.save(user)
    }

    suspend fun auth(request: AuthModel, encoder: PasswordEncoder): Any {
        return try {
            val user = repository.findUserByEmail(request.email)
            if (encoder.matches(request.password, user.password)) {
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
