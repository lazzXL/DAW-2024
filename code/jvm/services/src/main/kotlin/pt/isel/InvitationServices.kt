package pt.isel

import java.util.*

sealed class InvitationError {
    data object ChannelNotFound : InvitationError()
    data object UserNotFound: InvitationError()
    data object PermissionInvalid: InvitationError()

    data object UserNotInChannel : InvitationError()
}

class InvitationServices(
    private val trxManager: TransactionManager
) {


    fun createInvitation(
        channelID: UInt,
        permission: String,
        userId: UInt,
    ): Either<InvitationError, Invitation> = trxManager.run {
        // Check if channel exists
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(InvitationError.ChannelNotFound)
        // Chech if user exists
        val user = repoUser.findById(userId)
            ?: return@run failure(InvitationError.UserNotFound)
        // Check if sender is in the channel
        if(repoParticipant.isParticipant(channelID, userId)) return@run failure(InvitationError.UserNotInChannel)
        // Create channel
        val parsedPermission = permission.parseToPermission() ?: return@run failure(InvitationError.PermissionInvalid)
        val invitation:Invitation = repoInvite.createInvitation(UUID.randomUUID(), parsedPermission, channel)
        success(invitation)
    }


}



