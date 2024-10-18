package pt.isel.mem

import jakarta.inject.Named
import pt.isel.*

@Named
class RepositoryChannelInMem : RepositoryChannel{
    private val channels = mutableListOf<Channel>()

    override fun createChannel(name: String, description: String, adminID: UInt, visibility: Visibility): Channel =
        Channel(channels.count().toUInt(), name, adminID, description, visibility)

    override fun findByName(name: String): Channel? =
        channels.firstOrNull{name == it.name}

    override fun findAllByUser(userID: UInt, name: String?): List<Channel> {
        TODO()
    }

    override fun getPublicChannels(name: String?): List<Channel> =
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