package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class RegisterInvitationError {
    data object ChannelNotFound : RegisterInvitationError()
    data object UserNotFound: RegisterInvitationError()
    data object PermissionInvalid: RegisterInvitationError()
    data object UserNotInChannel : RegisterInvitationError()
}

@Named
class RegisterInvitationServices(
    private val trxManager: TransactionManager
) {


    fun createInvitation(
        token: UUID,
    ): Either<RegisterInvitationError, RegisterInvitation> = trxManager.run {
        /*// Chech if user exists
        val user = repoUser.findById(userId)
            ?: return@run failure(ChannelInvitationError.UserNotFound)*/
        val invitation: RegisterInvitation = repoRegisterInvitation.createInvitation(UUID.randomUUID())
        success(invitation)
    }


}



