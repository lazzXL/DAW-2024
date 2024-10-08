package pt.isel

interface RepositoryUser : Repository<User>{
    fun createUser(name : String, email : Email, password : String) : User

    fun emailExists(email: Email) : Boolean

    fun getUserbyEmail(email : Email) : User
}