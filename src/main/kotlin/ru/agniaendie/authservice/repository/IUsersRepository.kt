package ru.agniaendie.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.agniaendie.authservice.model.User
@Repository
interface IUsersRepository : JpaRepository<User, String>{
    fun save (user: User) : User
    fun findUserByUsername(username:String) : User
}
