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
        permission: Permission,
        userId: UInt,
    ): Either<ChannelInvitationError, ChannelInvitation> = trxManager.run {
        // Check if channel exists
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ChannelInvitationError.ChannelNotFound)
        // Check if sender is in the channel
        repoParticipant.isParticipant(channelID, userId)
            ?: return@run failure(ChannelInvitationError.UserNotInChannel)
        // Create channel
        val invitation:ChannelInvitation = repoChannelInvitation.createInvitation(UUID.randomUUID(), permission, channel)
        success(invitation)
    }


}



