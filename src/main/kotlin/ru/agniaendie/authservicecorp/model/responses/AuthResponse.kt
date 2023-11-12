package ru.agniaendie.authservicecorp.model.responses

import ru.agniaendie.authservicecorp.model.Role

data class AuthResponse(val username:String, val token:String, val uuid:String, val role: Role)
