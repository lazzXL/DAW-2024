package pt.isel

import java.util.*

interface RepositoryUser : Repository<User>{
    fun createUser(name : String, email : Email, token : UUID, password : String) : User
    fun findByEmail(email : Email) : User?
    fun findByName(name: String) : User?
}