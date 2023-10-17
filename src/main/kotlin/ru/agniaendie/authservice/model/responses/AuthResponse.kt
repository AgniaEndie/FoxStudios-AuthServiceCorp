package ru.agniaendie.authservice.model.responses

import ru.agniaendie.authservice.model.Role

data class AuthResponse(val username:String, val token:String, val uuid:String, val role: Role)
