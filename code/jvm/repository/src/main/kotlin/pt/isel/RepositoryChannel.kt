package pt.isel

import java.util.UUID

interface RepositoryChannel : Repository<Channel> {
    fun createChannel(name : String, description : String, adminID : UUID, visibility: Visibility) : Channel

}