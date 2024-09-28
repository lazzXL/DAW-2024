
interface RepositoryUser : Repository<User>{
    fun createUser(name : String, email : Email, password : String) : User

}