package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.time.LocalDateTime

class RepositoryMessageJdbi(
    private val handle: Handle,
) : RepositoryMessage {

    val defaultSkip = 0
    val defaultLimit = 20
    override fun sendMessage(content: String, date: LocalDateTime, participant: UInt): Message {
        val id =
            handle
                .createUpdate(
                    """
                INSERT INTO new.messages (content, date_sent, sender_id) 
                VALUES (:content, :date_sent, :sender_id)
                """,
                ).bind("content", content)
                .bind("date_sent", date)
                .bind("sender_id", participant.toInt())
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return Message(id.toUInt(), content, date, participant)
    }

    override fun getMessages(channel: Channel, limit : Int?, skip : Int?): List<Message> =
        handle
            .createQuery(
                """
                SELECT m.* FROM new.messages m
                JOIN new.participants p ON m.sender_id = p.id
                WHERE p.channel_id = :channel_id
                ORDER BY m.date_sent DESC
                LIMIT :limit OFFSET :skip
                """,
            ).bind("channel_id", channel.id.toInt())
            .bind("limit", limit ?: defaultLimit)
            .bind("skip", skip ?: defaultSkip)
            .map { rs, _ -> mapRowToMessage(rs) }
            .list()

    override fun findById(id: UInt): Message? =
        handle
            .createQuery(
                """
            SELECT m.* FROM new.messages m
            WHERE m.id = :id
            """,
            ).bind("id", id.toInt())
            .map { rs, _ -> mapRowToMessage(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<Message> =
        handle
            .createQuery(
                """
                SELECT m.* FROM new.messages m
                """,
            ).map { rs, _ -> mapRowToMessage(rs) }
            .list()


    override fun save(entity: Message) {
        handle
            .createUpdate(
                """
            UPDATE new.messages 
            SET content = :content, date_sent = :date_sent, sender_id = :sender_id
            WHERE id = :id
            """,
            ).bind("id", entity.id.toInt())
            .bind("content", entity.content)
            .bind("date_sent", entity.date)
            .bind("sender_id", entity.sender.toInt())
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM new.messages WHERE id = :id")
            .bind("id", id.toInt())
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM new.messages").execute()
    }

    private fun mapRowToMessage(rs: ResultSet): Message {
        return Message(
            id = rs.getInt("id").toUInt(),
            content = rs.getString("content"),
            date = rs.getTimestamp("date_sent").toLocalDateTime(),
            sender = rs.getInt("sender_id").toUInt(),
        )
    }

}