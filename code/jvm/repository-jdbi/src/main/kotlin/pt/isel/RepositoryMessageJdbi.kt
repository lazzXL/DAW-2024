package pt.isel

import org.jdbi.v3.core.Handle
import java.time.LocalDateTime

class RepositoryMessageJdbi(
    private val handle: Handle,
) : RepositoryMessage {
    override fun sendMessage(content: String, date: LocalDateTime, participant: Participant): Message {
        TODO("Not yet implemented")
    }

    override fun getMessages(channel: Channel, numOfMessages: UInt): List<Message> {
        TODO("Not yet implemented")
    }

    override fun findById(id: UInt): Message? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Message> {
        TODO("Not yet implemented")
    }

    override fun save(entity: Message) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UInt) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.messages").execute()
    }

}