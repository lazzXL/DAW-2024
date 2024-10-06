package pt.isel

import java.util.UUID

sealed class ChannelError {
    data object ChannelNotFound : ChannelError()
    data object ChannelNameAlreadyExists: ChannelError()
    data object AdminNotFound: ChannelError()
    data object UserNotFound: ChannelError()
    data object InvalidInvite: ChannelError()

}

class ChannelServices(
    private val trxManager: TransactionManager
) {

    private val defaultPublicPermission = Permission.READ_ONLY

    fun createChannel(
        name: String,
        description: String,
        adminId: UUID,
        visibility: Visibility
    ): Either<ChannelError, Channel> = trxManager.run {
        // Check if channel name is unique
        if (repoChannel.findByName(name) != null) return@run failure(ChannelError.ChannelNameAlreadyExists)
        // Check if admin exists
        val admin = repoUser.findById(adminId)
            ?: return@run failure(ChannelError.AdminNotFound)
        // Create channel
        val channel = repoChannel.createChannel(name, description, admin, visibility)
        success(channel)
    }

    fun getChannel(
        channelId: UUID
    ): Either<ChannelError, Channel> = trxManager.run {
        repoChannel
            .findById(channelId)
            ?.let { success(it) }
            ?: failure(ChannelError.ChannelNotFound)
    }

    fun getJoinedChannels(
        userID : UUID
    ): Either<ChannelError, List<Channel>> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ChannelError.UserNotFound)
        success(repoChannel.findAllByUser(user))
    }

    fun joinChannelByInvite(
        userID:UUID,
        code:UUID
    ): Either<ChannelError,Channel> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ChannelError.UserNotFound)
        val invite = repoInvite.findByCode(code)
            ?: return@run failure(ChannelError.InvalidInvite)
        val channel = invite.channel
        success(repoParticipant.joinChannel(channel,user,invite.permission))
    }

    fun joinPublicChannel(
        userID:UUID,
        channelID:UUID
    ): Either<ChannelError,Channel> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ChannelError.UserNotFound)
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ChannelError.ChannelNotFound)
        success(repoParticipant.joinChannel(channel,user,defaultPublicPermission))
    }

    fun leaveChannel(
        userID:UUID,
        channelID: UUID
    ) : Either<ChannelError,Channel> = trxManager.run {
        val user = repoUser.findById(userID)
            ?: return@run failure(ChannelError.UserNotFound)
        val channel = repoChannel.findById(channelID)
            ?: return@run failure(ChannelError.ChannelNotFound)
        success(repoParticipant.leaveChannel(channel,user))
    }

    fun getPublicChannels(): Either<ChannelError,List<Channel>> = trxManager.run {
        success(repoChannel.getPublicChannels())
    }



}


