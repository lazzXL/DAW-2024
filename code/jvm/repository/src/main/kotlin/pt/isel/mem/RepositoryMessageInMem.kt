package pt.isel.mem

import pt.isel.Channel
import pt.isel.Message
import pt.isel.RepositoryMessage
import java.time.LocalDateTime

class RepositoryMessageInMem : RepositoryMessage {
    private val messages = mutableListOf<Message>()

    override fun findById(id: UInt): Message? =
        messages.firstOrNull{it.id == id}


    override fun findAll(): List<Message> =
        messages.toList()

    override fun save(entity: Message) {
        messages.removeIf { it.id == entity.id }
        messages.add(entity)
    }

    override fun deleteById(id: UInt) {
        messages.removeIf { it.id == id }
    }

    override fun clear() =
        messages.clear()

    override fun sendMessage(content: String, date: LocalDateTime, participant: UInt): Message =
        Message(messages.count().toUInt(), content, date, participant)

    override fun getMessages(channel: Channel, numOfMessages: UInt): List<Message> =
        TODO()
}