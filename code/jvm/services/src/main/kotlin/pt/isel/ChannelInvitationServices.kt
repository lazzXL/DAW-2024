package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class ChannelInvitationError {
    data object ChannelNotFound : ChannelInvitationError()
    data object UserNotFound: ChannelInvitationError()
    data object PermissionInvalid: ChannelInvitationError()
    data object UserNotInChannel : ChannelInvitationError()
}

@Named
class ChannelInvitationServices(
    private val trxManager: TransactionManager
) {


    fun createInvitation(
        channelID: UInt,
        permission: String,
    ): Either<ChannelInvitationError, ChannelInvitation> = trxManager.run {
        // Check if channel exists
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ChannelInvitationError.ChannelNotFound)
        // Chech if user exists
        /*val user = repoUser.findById(userId)
            ?: return@run failure(ChannelInvitationError.UserNotFound)
        // Check if sender is in the channel
        if(repoParticipant.isParticipant(channelID, userId)) return@run failure(ChannelInvitationError.UserNotInChannel)
        */// Create channel
        val parsedPermission = permission.parseToPermission() ?: return@run failure(ChannelInvitationError.PermissionInvalid)
        val invitation:ChannelInvitation = repoChannelInvitation.createInvitation(UUID.randomUUID(), parsedPermission, channel)
        success(invitation)
    }


}



