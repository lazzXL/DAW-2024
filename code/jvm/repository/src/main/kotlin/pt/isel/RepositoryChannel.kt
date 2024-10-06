package pt.isel

import java.util.UUID

interface RepositoryChannel : Repository<Channel> {
    fun createChannel(name : String, description : String, admin : User, visibility: Visibility) : Channel

    fun findByName(name : String) : Channel?
    fun findAllByUser(user: User): List<Channel>
    fun getPublicChannels(): List<Channel>

}