package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class ChannelInvitationError {
    data object ChannelNotFound : ChannelInvitationError()
    data object UserNotFound: ChannelInvitationError()
    data object PermissionInvalid: ChannelInvitationError()
    data object UserIsNotAdmin : ChannelInvitationError()
    data object InvitationNotFound : ChannelInvitationError()
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
        // Check if sender is an admin
        if(userId!=channel.adminID)
            return@run failure(ChannelInvitationError.UserIsNotAdmin)
        // Create channel
        val invitation:ChannelInvitation = repoChannelInvitation.createInvitation(UUID.randomUUID(), permission, channel)
        success(invitation)
    }

    fun deleteInvitation(
        invitationId: UInt,
        userId: UInt,
    ): Either<ChannelInvitationError, Unit> = trxManager.run {
        // Check if invitation exists
        val invitation = repoChannelInvitation.findById(invitationId)
            ?: return@run failure(ChannelInvitationError.InvitationNotFound)
        // Check if sender is an admin
        if(userId!=invitation.channel.adminID)
            return@run failure(ChannelInvitationError.UserIsNotAdmin)
        // Delete invitation
        repoChannelInvitation.deleteById(invitationId)
        success(Unit)
    }


}



