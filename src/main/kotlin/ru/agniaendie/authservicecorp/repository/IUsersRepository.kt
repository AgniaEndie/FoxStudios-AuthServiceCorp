package ru.agniaendie.authservicecorp.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.agniaendie.authservicecorp.model.User
@Repository
interface IUsersRepository : JpaRepository<User, String>{
    fun save (user: User) : User
    fun findUserByUsername(username:String) : User
    fun findUserByEmail(email:String) : User
}
