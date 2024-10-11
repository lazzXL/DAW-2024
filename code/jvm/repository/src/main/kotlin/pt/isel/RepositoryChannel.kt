package pt.isel

import java.util.UUID

interface RepositoryChannel : Repository<Channel> {
    fun createChannel(name : String, description : String, adminID : UInt, visibility: Visibility) : Channel

    fun findByName(name : String) : Channel?
    fun findAllByUser(userID: UInt): List<Channel>
    fun getPublicChannels(): List<Channel>



}