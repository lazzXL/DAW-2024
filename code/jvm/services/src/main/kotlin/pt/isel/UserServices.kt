package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class UserError {

    data object UserNotFound : UserError()
    data object EmailNotFound : UserError()
    data object IncorrectPassword : UserError()
    data object EmailAlreadyExists: UserError()
    data object PasswordsDoNotMatch: UserError()
    data object UsernameAlreadyExists: UserError()

}

@Named
class UserServices(
    private val trxManager: TransactionManager
){
    //login
    fun login(
        name : String,
        password: String
    ) : Either<UserError, UUID> = trxManager.run {
        val user = repoUser.findByName(name) ?: return@run failure(UserError.UserNotFound)
        //check if passwords correspond
        if (password != user.password)
            return@run failure(UserError.IncorrectPassword)
        success(user.token)
    }

    //registration
    fun registration(
        email : Email,
        name: String,
        password: String,
    ) : Either<UserError, User> = trxManager.run {
        if (repoUser.findByEmail(email) != null)
            return@run failure(UserError.EmailAlreadyExists)
        if (repoUser.findByName(name) != null)
            return@run failure(UserError.UsernameAlreadyExists)
        val token = UUID.randomUUID()
        val user = repoUser.createUser(name, email, token, password)

        success(user)
    }
    //change password
    fun changePassword(
        email : Email,
        password : String,
        newPassword : String
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findByEmail(email) ?: return@run failure(UserError.EmailNotFound)
        if(user.password != password) return@run failure(UserError.PasswordsDoNotMatch)
        val updatedUser = user.copy(password = newPassword)
        repoUser.save(updatedUser)
        success(updatedUser)
    }

    //change email
    fun changeEmail(
        email : Email,
        newEmail : Email
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findByEmail(email) ?: return@run failure(UserError.EmailNotFound)
        if (repoUser.findByEmail(newEmail) != null)
            return@run failure(UserError.EmailAlreadyExists)
        val updatedUser = user.copy(email = newEmail)
        repoUser.save(updatedUser)
        success(updatedUser)
    }
    //change username
    fun changeUsername(
        userId : UInt,
        newUsername : String
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        if (repoUser.findByName(newUsername) != null)
            return@run failure(UserError.UsernameAlreadyExists)
        val updatedUser = user.copy(name = newUsername)
        repoUser.save(updatedUser)
        success(updatedUser)
    }
    //get user info
    fun getUserInfo(
        userId : UInt
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        success(user)
    }
}