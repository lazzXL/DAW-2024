package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.util.*

class RepositoryParticipantJdbi(
    private val handle: Handle,
) : RepositoryParticipant {

    override fun createParticipant(
        user: User,
        channel: Channel,
        permission: Permission
    ): Participant {
        val id =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.participants (user_id, channel_id, permission )
                VALUES (:user_id, :channel_id, :permission)
                """,
                ).bind("user_id", user.id)
                .bind("channel_id", channel.id)
                .bind("permission", permission.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(UInt::class.java)
                .one()

        return Participant(id, user, channel, permission )
    }

    override fun isParticipant(channelId: UInt, userId: UInt): Boolean {
        val i = handle
            .createQuery(
                """
                SELECT p.* FROM dbo.participants p
                JOIN dbo.users u ON p.user_id = u.id
                JOIN dbo.channels c ON p.channel_id = c.id
                WHERE p.channel_id = :channelId
                AND p.user_id = :userId
                """,
            ).bind("channelId", channelId.toInt())
            .bind("userId", userId.toInt())
            .map { rs, _ -> mapRowToParticipant(rs) }
            .findOne()
            .orElse(null)
        return true
    }


    override fun findById(id: UInt): Participant? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Participant> {
        TODO("Not yet implemented")
    }

    override fun save(entity: Participant) {
        handle
            .createUpdate(
                """
                UPDATE dbo.participants
                SET user_id = :user_id, channel_id = :channel_id, permission = :permission
                WHERE id = :id
                """,
            ).bind("id", entity.id)
            .bind("user_id", entity.user.id)
            .bind("channel_id", entity.channel.id)
            .bind("permission", entity.permission.name)
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.participants WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.participants").execute()
    }

    private fun mapRowToParticipant(rs: ResultSet): Participant {
        val user = User(
            rs.getInt("user_id").toUInt(),
            UUID.fromString(rs.getString("token")),
            rs.getString("u.name"),
            Email(rs.getString("email")),
            rs.getString("password"))
        val channel = Channel(
            rs.getInt("channel_id").toUInt(),
            rs.getString("c.name"),
            rs.getInt("admin_id").toUInt(),
            rs.getString("description"),
            Visibility.valueOf(rs.getString("visibility"))
        )
        return Participant(
            id = rs.getInt("id").toUInt(),
            user = user,
            channel = channel,
            permission = Permission.valueOf(rs.getString("permission"))
        )
    }

}