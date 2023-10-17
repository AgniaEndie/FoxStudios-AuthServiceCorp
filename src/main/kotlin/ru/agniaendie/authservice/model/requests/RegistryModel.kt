package ru.agniaendie.authservice.model.requests

data class RegistryModel(val username:String,val password:String, val firstname:String, val lastname:String,val role: Int)
