package pt.isel

import java.util.*

sealed class UserError {
    data object UserNotFound : ChannelError()
    data object EmailNotFound : UserError()
    data object IncorrectPassword : UserError()
    data object EmailAlreadyExists: UserError()
    /*data object AdminNotFound: ChannelError()
    data object UserNotFound: ChannelError()
    data object InvalidInvite: ChannelError()*/

}

class UserServices(
    private val trxManager: TransactionManager
){
    //login
    fun login(
        email : Email,
        password: String
    ) : Either<UserError, UUID> = trxManager.run {
        //check if there is a user with this email
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
        newPassoword : String
    ) : Either<UserError, User> = trxManager.run{
        TODO()
    }



    //change email

    //change username

    //get user info
}