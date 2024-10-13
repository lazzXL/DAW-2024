package pt.isel

import jakarta.inject.Named
import java.util.UUID

sealed class ChannelError {
    data object ChannelNotFound : ChannelError()
    data object ChannelNameAlreadyExists: ChannelError()

    data object ChannelNotPublic : ChannelError()
    data object AdminNotFound: ChannelError()
    data object UserNotFound: ChannelError()
    data object InvalidInvite: ChannelError()

}

@Named
class ChannelServices(
    private val trxManager: TransactionManager
) {

    fun createChannel(
        name: String,
        description: String,
        adminId: UInt,
        visibility: Visibility
    ): Either<ChannelError, Channel> = trxManager.run {
        // Check if channel name is unique
        if (repoChannel.findByName(name) != null) return@run failure(ChannelError.ChannelNameAlreadyExists)
        // Check if admin exists
        repoUser.findById(adminId)
            ?: return@run failure(ChannelError.AdminNotFound)
        // Create channel
        val channel = repoChannel.createChannel(name, description, adminId, visibility)
        success(channel)
    }

    fun getChannel(
        channelId: UInt
    ): Either<ChannelError, Channel> = trxManager.run {
        repoChannel
            .findById(channelId)
            ?.let { success(it) }
            ?: failure(ChannelError.ChannelNotFound)
    }

    fun getJoinedChannels(
        userID : UInt
    ): Either<ChannelError, List<Channel>> = trxManager.run {
        repoUser.findById(userID)
            ?: return@run failure(ChannelError.UserNotFound)
        success(repoChannel.findAllByUser(userID))
    }


    fun getPublicChannels(): Either<ChannelError,List<Channel>> = trxManager.run {
        success(repoChannel.getPublicChannels())
    }



}


