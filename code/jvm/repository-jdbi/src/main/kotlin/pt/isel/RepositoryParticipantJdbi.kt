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
        val id = try {
            handle
                .createUpdate(
                    """
                INSERT INTO new.participants (user_id, channel_id, permission, is_active)
                VALUES (:user_id, :channel_id, :permission, TRUE)
                """,
                )
                .bind("user_id", user.id.toInt())
                .bind("channel_id", channel.id.toInt())
                .bind("permission", permission.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()
        } catch (e: Exception) {
            // If insertion fails due to trigger behavior, fetch the existing ID
            handle
                .createQuery(
                    """
                SELECT id FROM new.participants 
                WHERE user_id = :user_id AND channel_id = :channel_id
                """
                )
                .bind("user_id", user.id.toInt())
                .bind("channel_id", channel.id.toInt())
                .mapTo(Int::class.java)
                .one()
        }

        return Participant(id.toUInt(), user, channel, permission)
    }


    override fun getParticipantsFromChannel(channelId: UInt): List<Participant> =
        handle
            .createQuery(
                """
                SELECT p.*, c.name as channel_name, c.admin_id, c.description, c.visibility,
                u.name as user_name, u.email, u.password
                FROM new.participants p
                JOIN new.users u ON p.user_id = u.id
                JOIN new.channels c ON p.channel_id = c.id
                WHERE p.channel_id = :channelId AND p.is_active = TRUE
                """,
            ).bind("channelId", channelId.toInt())
            .map { rs, _ -> mapRowToParticipant(rs) }
            .list()


    override fun isParticipant(channelId: UInt, userId: UInt): Participant? =
        handle
            .createQuery(
                """
                SELECT p.*, c.name as channel_name, c.admin_id, c.description, c.visibility,
                u.name as user_name, u.email, u.password
                FROM new.participants p
                JOIN new.users u ON p.user_id = u.id
                JOIN new.channels c ON p.channel_id = c.id
                WHERE p.channel_id = :channelId
                AND p.user_id = :userId
                AND p.is_active = TRUE
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
                FROM new.participants p
                JOIN new.channels c ON p.channel_id = c.id
                JOIN new.users u ON p.user_id = u.id
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
                FROM new.participants p
                JOIN new.channels c ON p.channel_id = c.id
                JOIN new.users u ON p.user_id = u.id
                """)
            .map { rs, _ -> mapRowToParticipant(rs) }
            .list()


    override fun save(entity: Participant) {
        handle
            .createUpdate(
                """
                UPDATE new.participants
                SET user_id = :user_id, channel_id = :channel_id, permission = :permission
                WHERE id = :id
                """,
            ).bind("id", entity.id.toInt())
            .bind("user_id", entity.user.id.toInt())
            .bind("channel_id", entity.channel.id.toInt())
            .bind("permission", entity.permission.name)
            .execute()
    }

    override fun setInactiveParticipant(id: UInt) {
        handle
            .createUpdate(
                """
                UPDATE new.participants
                SET is_active = FALSE
                WHERE id = :id
                """,
            ).bind("id", id.toInt())
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM new.participants WHERE id = :id")
            .bind("id", id.toInt())
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM new.participants").execute()
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