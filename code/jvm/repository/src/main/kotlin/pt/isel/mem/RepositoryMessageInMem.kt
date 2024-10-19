package pt.isel.mem

import pt.isel.Channel
import pt.isel.Message
import pt.isel.RepositoryMessage
import java.time.LocalDateTime

val messages = mutableListOf<Message>()

class RepositoryMessageInMem : RepositoryMessage {

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

    override fun sendMessage(content: String, date: LocalDateTime, participant: UInt): Message {
        val msg = Message(messages.count().toUInt(), content, date, participant)
        messages.add(msg)
        return msg
    }

    override fun getMessages(channel: Channel, limit: Int?, skip: Int?): List<Message> {
        val participants = participants.filter { it.channel == channel }
        return messages.filter {msg -> participants.any { it.id == msg.sender } }.drop(skip ?: 0).take(limit ?: 20)
    }
}