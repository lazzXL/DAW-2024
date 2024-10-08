package pt.isel

sealed class UserError {
    data object UserNotFound : ChannelError()
    data object UserNameAlreadyExists: ChannelError()
    /*data object AdminNotFound: ChannelError()
    data object UserNotFound: ChannelError()
    data object InvalidInvite: ChannelError()*/

}

class UserServices(
    private val trxManager: TransactionManager
){

    //login

    //registration

    //change password

    //change email

    //change username

    //get user info
}