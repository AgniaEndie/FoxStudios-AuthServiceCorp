package ru.agniaendie.authservicecorp.model.requests

data class RegistryModel(val username:String,val password:String, val firstname:String,val surname:String, val email:String, val organisation:String, val lastname:String,val role: Int)
