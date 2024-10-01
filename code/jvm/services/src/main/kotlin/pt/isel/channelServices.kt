package pt.isel

import java.util.UUID

class ChannelServices(
    private val channelRepository: RepositoryChannel,
    private val userRepository: RepositoryUser,
){
    fun createChannel(name : String, description: String, admin : UUID, visibility: Visibility) : Channel{
        val channel = channelRepository.createChannel(name, description, admin, visibility)
        return channel
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


