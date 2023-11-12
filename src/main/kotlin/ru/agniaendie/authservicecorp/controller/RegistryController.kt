package ru.agniaendie.authservicecorp.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.agniaendie.authservicecorp.model.requests.AuthModel
import ru.agniaendie.authservicecorp.model.requests.RegistryModel
import ru.agniaendie.authservicecorp.repository.IUsersRepository
import ru.agniaendie.authservicecorp.service.AuthService

@RestController
@RequestMapping("main")
class RegistryController(
    @Autowired var repository: IUsersRepository,
    @Value("\${jwt.secret}") secret: String?,
    @Autowired var passwordEncoder: PasswordEncoder
) {

    val service by lazy { AuthService(repository, secret) }

    @GetMapping("test")
    suspend fun test(): String {
        println("test")
        return service.test()
    }

    @GetMapping("test1")
    suspend fun test1(): String {
        println("test1")
        return service.test()
    }

    @PostMapping("registry")
    suspend fun registry(@RequestBody request: RegistryModel): Any {
        return service.registry(request, passwordEncoder)
    }

    @PostMapping("auth")
    suspend fun auth(@RequestBody request: AuthModel): Any {
        return service.auth(request, passwordEncoder)
    }

}
