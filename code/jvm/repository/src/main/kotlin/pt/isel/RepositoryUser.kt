package pt.isel

import java.util.*

interface RepositoryUser : Repository<User>{
    fun createUser(name : String, email : Email, token : UUID, password : String) : User

    //fun emailExists(email: Email) : Boolean

    fun findByEmail(email : Email) : User?

    //fun updateUser(user : User, newName : String? = null, newEmail: Email? = null, newPassword: String? = null) : User
}