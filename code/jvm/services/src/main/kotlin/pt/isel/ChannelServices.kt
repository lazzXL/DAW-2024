package pt.isel

import java.util.UUID

sealed class ChannelError {
    data object ChannelNotFound : ChannelError()
}

class ChannelServices(
    private val trxManager: TransactionManager
){
    fun createChannel(name : String, description: String, admin : UUID, visibility: Visibility) : Either<ChannelError, Channel> = trxManager.run {
        val channel = repoChannel.createChannel(name, description, admin, visibility) ?: return@run failure(ChannelError.ChannelNotFound)
        success(channel)
    }

//    fun getChannel(id : UUID): Channel{
//        val channel = channelRepository.findById(id)
//        return channel
//    }
//
//    fun getJoinedChannels(userID : UUID) : Channel{
//        val channels =
//    }


}


