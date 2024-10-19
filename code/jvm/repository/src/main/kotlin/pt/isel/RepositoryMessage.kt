package pt.isel

import java.time.LocalDateTime

interface RepositoryMessage : Repository<Message> {
    fun sendMessage(content : String, date : LocalDateTime, participant: UInt) : Message

    fun getMessages(channel: Channel, limit : Int? = null, skip : Int? = null): List<Message>

}