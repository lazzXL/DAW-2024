package pt.isel

import java.util.*

sealed class UserError {

    data object UserNotFound : UserError()
    data object EmailNotFound : UserError()
    data object IncorrectPassword : UserError()
    data object EmailAlreadyExists: UserError()

}

class UserServices(
    private val trxManager: TransactionManager
){
    //login
    fun login(
        email : Email,
        password: String
    ) : Either<UserError, UUID> = trxManager.run {
        if (!repoUser.emailExists(email))
            return@run failure(UserError.EmailNotFound)
        val user = repoUser.getUserbyEmail(email)
        //check if passwords correspond
        if (password != user.password)
            return@run failure(UserError.IncorrectPassword)
        success(user.token)
    }

    //registration
    fun registration(
        name: String,
        email : Email,
        password: String,
    ) : Either<UserError, User> = trxManager.run {
        //checks if email does not exist
        if (repoUser.emailExists(email))
            return@run failure(UserError.EmailAlreadyExists)

        val user = repoUser.createUser(name, email, password)

        success(user)
    }
    //change password
    fun changePassword(
        email : Email,
        password : String,
        newPassword : String
    ) : Either<UserError, User> = trxManager.run {
        if (!repoUser.emailExists(email)) return@run failure(UserError.EmailNotFound)
        val user = repoUser.getUserbyEmail(email)
        val newUser = repoUser.updateUser(user, newPassword = newPassword)
        success(newUser)
    }

    //change email
    fun changeEmail(
        email : Email,
        newEmail : Email
    ) : Either<UserError, User> = trxManager.run {
        if (!repoUser.emailExists(email)) return@run failure(UserError.EmailNotFound)
        val user = repoUser.getUserbyEmail(email)
        val newUser = repoUser.updateUser(user, newEmail = newEmail)
        success(newUser)
    }
    //change username
    fun changeUsername(
        userId : UInt,
        newUsername : String
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        val newUser = repoUser.updateUser(user, newName = newUsername)
        success(newUser)
    }
    //get user info
    fun getUserInfo(
        userId : UInt
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        success(user)
    }
}