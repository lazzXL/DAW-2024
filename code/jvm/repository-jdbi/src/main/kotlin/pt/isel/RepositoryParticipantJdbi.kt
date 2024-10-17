package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet

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
                ).bind("user_id", user.id.toInt())
                .bind("channel_id", channel.id.toInt())
                .bind("permission", permission.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return Participant(id.toUInt(), user, channel, permission )
    }

    override fun isParticipant(channelId: UInt, userId: UInt): Participant? =
        handle
            .createQuery(
                """
                SELECT p.*, c.name as channel_name, c.admin_id, c.description, c.visibility,
                u.name as user_name, u.email, u.password
                FROM dbo.participants p
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


    override fun findById(id: UInt): Participant? {
        return handle
            .createQuery(
                """
                SELECT p.*, c.name as channel_name, c.admin_id, c.description, c.visibility,
                u.name as user_name, u.email, u.password
                FROM dbo.participants p
                JOIN dbo.channels c ON p.channel_id = c.id
                JOIN dbo.users u ON p.user_id = u.id
                WHERE p.id = :id
                """,
            )
            .bind("id", id.toInt())
            .map { rs, _ -> mapRowToParticipant(rs) }
            .findOne()
            .orElse(null)
    }

    override fun findAll(): List<Participant> =
        handle
            .createQuery(
                """
                SELECT p.*, c.name as channel_name, c.admin_id, c.description, c.visibility,
                u.name as user_name, u.email, u.password
                FROM dbo.participants p
                JOIN dbo.channels c ON p.channel_id = c.id
                JOIN dbo.users u ON p.user_id = u.id
                """)
            .map { rs, _ -> mapRowToParticipant(rs) }
            .list()


    override fun save(entity: Participant) {
        handle
            .createUpdate(
                """
                UPDATE dbo.participants
                SET user_id = :user_id, channel_id = :channel_id, permission = :permission
                WHERE id = :id
                """,
            ).bind("id", entity.id.toInt())
            .bind("user_id", entity.user.id.toInt())
            .bind("channel_id", entity.channel.id.toInt())
            .bind("permission", entity.permission.name)
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.participants WHERE id = :id")
            .bind("id", id.toInt())
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.participants").execute()
    }

    private fun mapRowToParticipant(rs: ResultSet): Participant {
        val user = User(
            rs.getInt("user_id").toUInt(),
            rs.getString("user_name"),
            Email(rs.getString("email")),
            PasswordValidationInfo(rs.getString("password")))
        val channel = Channel(
            rs.getInt("channel_id").toUInt(),
            rs.getString("channel_name"),
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