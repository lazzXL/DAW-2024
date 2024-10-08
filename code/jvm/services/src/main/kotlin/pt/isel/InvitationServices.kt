package pt.isel

import java.util.*

sealed class InvitationError {
    data object ChannelNotFound : InvitationError()
    data object UserNotFound: InvitationError()
    data object PermissionInvalid: InvitationError()
    /*data object ChannelNameAlreadyExists: ChannelError()
    data object AdminNotFound: ChannelError()
    data object UserNotFound: ChannelError()
    data object InvalidInvite: ChannelError()*/

}

class InvitationServices(
    private val trxManager: TransactionManager
) {


    fun createInvitation(
        channelID: UInt,
        permission: String,
        senderId: UInt,
    ): Either<InvitationError, Invitation> = trxManager.run {
        // Check if channel exists
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(InvitationError.ChannelNotFound)
        // Chech if user exists
        val user = repoUser.findById(senderId)
            ?: return@run failure(InvitationError.UserNotFound)
        // Check if sender is in the channel
        // TODO
        // Create channel
        val parsedPermission = permission.parseToPermission() ?: return@run failure(InvitationError.PermissionInvalid)
        val invitation:Invitation = repoInvite.createInvitation(UUID.randomUUID(), parsedPermission, channel)
        success(invitation)
    }
}



