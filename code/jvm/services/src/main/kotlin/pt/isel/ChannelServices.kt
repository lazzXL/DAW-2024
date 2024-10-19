package pt.isel

import jakarta.inject.Named

sealed class ChannelError {
    data object ChannelNotFound : ChannelError()
    data object ChannelNameAlreadyExists: ChannelError()
    data object ChannelNotPublic : ChannelError()


}

@Named
class ChannelServices(
    private val trxManager: TransactionManager
) {
    private val adminPermission = Permission.READ_WRITE

    fun createChannel(
        name: String,
        description: String,
        admin: User,
        visibility: Visibility
    ): Either<ChannelError, Channel> = trxManager.run {
        if (repoChannel.findByName(name) != null) return@run failure(ChannelError.ChannelNameAlreadyExists)
        val channel = repoChannel.createChannel(name, description, admin.id, visibility)
        repoParticipant.createParticipant(admin,channel, adminPermission)
        success(channel)
    }

    fun getChannel(
        channelId: UInt,
        userID: UInt
    ): Either<ChannelError, Channel> = trxManager.run {
        val channel = repoChannel.findById(channelId) ?: return@run failure(ChannelError.ChannelNotFound)
        if(channel.visibility == Visibility.PRIVATE && repoParticipant.isParticipant(channelId,userID) == null){
            return@run failure(ChannelError.ChannelNotPublic)
        }
        success(channel)
    }

    fun getJoinedChannels(
        userID : UInt,
        name : String? = null,
        limit: Int? = null,
        skip: Int? = null
    ): Either<ChannelError, List<Channel>> = trxManager.run {
        success(repoChannel.findAllByUser(userID,name,limit,skip))
    }


    fun getPublicChannels(
        name: String? = null,
        limit: Int? = null,
        skip: Int? = null
    )
    : Either<ChannelError,List<Channel>> = trxManager.run {
        success(repoChannel.getPublicChannels(name,limit,skip))
    }



}


