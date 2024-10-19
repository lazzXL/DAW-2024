package pt.isel.mem

import jakarta.inject.Named
import pt.isel.*

val channels = mutableListOf<Channel>()


@Named
class RepositoryChannelInMem : RepositoryChannel{

    override fun createChannel(name: String, description: String, adminID: UInt, visibility: Visibility): Channel {
        val channel = Channel(channels.count().toUInt(), name, adminID, description, visibility)
        channels.add(channel)
        return channel
    }

    override fun findByName(name: String): Channel? =
        channels.firstOrNull{name == it.name}

    override fun findAllByUser(userID: UInt, name: String?, limit: Int?, skip: Int?): List<Channel> {
        val participants = participants.filter { it.user.id == userID }
        return participants.map { it.channel }.drop(skip ?: 0).take(limit ?: 20)
    }

    override fun getPublicChannels(name: String?, limit: Int?, skip: Int?): List<Channel> =
        channels.filter { it.visibility == Visibility.PUBLIC }

    override fun findById(id: UInt): Channel? =
        channels.firstOrNull{it.id == id}


    override fun findAll(): List<Channel> =
        channels.toList()


    override fun save(entity: Channel) {
        channels.removeIf { it.id == entity.id }
        channels.add(entity)
    }

    override fun deleteById(id: UInt) {
        channels.removeIf { it.id == id }
    }

    override fun clear() =
        channels.clear()
}