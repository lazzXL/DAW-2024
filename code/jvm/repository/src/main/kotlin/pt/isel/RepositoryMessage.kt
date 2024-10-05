package pt.isel

import java.time.LocalDateTime
import java.util.UUID

interface RepositoryMessage : Repository<Message> {
    fun sendMessage(content : String, date : LocalDateTime, user : UUID, channelID: UUID) : Message
}