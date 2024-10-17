package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class ParticipantError{
    data object ParticipantNotFound: ParticipantError()

    data object ChannelNotPublic: ParticipantError()

    data object UserNotFound: ParticipantError()

    data object ChannelNotFound: ParticipantError()

    data object InvalidInvite: ParticipantError()

    data object UserAlreadyInChannel: ParticipantError()
}

@Named
class ParticipantServices (
    private val trxManager: TransactionManager
){
    private val defaultPublicPermission = Permission.READ_ONLY

    fun joinChannelByInvite(
        user: User,
        code: UUID
    ): Either<ParticipantError, Participant> = trxManager.run {
        val invite = repoChannelInvitation.findByCode(code)
            ?: return@run failure(ParticipantError.InvalidInvite)
        val channel = invite.channel
        if(repoParticipant.isParticipant(channel.id,user.id) != null)
            return@run failure(ParticipantError.UserAlreadyInChannel)
        val participant = repoParticipant.createParticipant(user, channel, invite.permission)
        repoChannelInvitation.deleteById(invite.id)
        success(participant)
    }

    fun joinPublicChannel(
        user: User,
        channelID: UInt
    ): Either<ParticipantError, Participant> = trxManager.run {
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ParticipantError.ChannelNotFound)
        if (channel.visibility == Visibility.PRIVATE) return@run failure(ParticipantError.ChannelNotPublic)
        if(repoParticipant.isParticipant(channel.id,user.id) != null)
            return@run failure(ParticipantError.UserAlreadyInChannel)
        success(repoParticipant.createParticipant(user, channel, defaultPublicPermission))
    }

    fun leaveChannel(
        channelId: UInt,
        userId: UInt,
    ): Either<ParticipantError, Unit> = trxManager.run {
        val participant = repoParticipant.isParticipant(channelId,userId)
            ?: return@run failure(ParticipantError.ParticipantNotFound)
        success(repoParticipant.deleteById(participant.id))
    }
}
