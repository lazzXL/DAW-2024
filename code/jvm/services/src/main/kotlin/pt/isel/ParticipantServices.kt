package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class ParticipantError{
    data object ParticipantNotFound: ParticipantError()

    data object ChannelNotPublic: ParticipantError()

    data object UserNotFound: ParticipantError()

    data object ChannelNotFound: ParticipantError()

    data object InvalidInvite: ParticipantError()
}

@Named
class ParticipantServices (
    private val trxManager: TransactionManager
){
    private val defaultPublicPermission = Permission.READ_ONLY

    fun joinChannelByInvite(
        userID: UInt,
        code: UUID
    ): Either<ParticipantError, Participant> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ParticipantError.UserNotFound)
        val invite = repoChannelInvitation.findByCode(code)
            ?: return@run failure(ParticipantError.InvalidInvite)
        val channel = invite.channel
        success(repoParticipant.createParticipant(user, channel, invite.permission))
    }

    fun joinPublicChannel(
        userID: UInt,
        channelID: UInt
    ): Either<ParticipantError, Participant> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ParticipantError.UserNotFound)
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ParticipantError.ChannelNotFound)
        if (channel.visibility == Visibility.PRIVATE) return@run failure(ParticipantError.ChannelNotPublic)
        success(repoParticipant.createParticipant(user, channel, defaultPublicPermission))
    }

    fun leaveChannel(
        participantID: UInt
    ): Either<ParticipantError, Unit> = trxManager.run {
        repoParticipant.findById(participantID) ?: return@run failure(ParticipantError.ParticipantNotFound)
        success(repoParticipant.deleteById(participantID))
    }
}
