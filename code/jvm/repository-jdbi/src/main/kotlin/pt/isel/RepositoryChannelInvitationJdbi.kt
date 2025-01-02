package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.util.*

class RepositoryChannelInvitationJdbi(
    private val handle: Handle,
) : RepositoryChannelInvitation {

    override fun findByCode(code: UUID): ChannelInvitation? =
        handle
            .createQuery(
                """
                SELECT i.*,
                c.id as c_id,
                c.name,
                c.description,
                c.admin_id,
                c.visibility
                FROM new.channel_invitations i
                JOIN new.channels c ON i.channel_id = c.id 
                WHERE i.code = :code
            """,
            ).bind("code", code.toString())
            .map { rs, _ -> mapRowToInvitation(rs) }
            .findOne()
            .orElse(null)

    override fun createInvitation(code: UUID, permission: Permission, channel: Channel): ChannelInvitation {
        val id =
            handle
                .createUpdate(
                    """
                INSERT INTO new.channel_invitations (code, channel_id, permission) 
                VALUES (:code, :channel_id, :permission)
                """,
                ).bind("code", code.toString())
                .bind("channel_id", channel.id.toInt())
                .bind("permission", permission.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return ChannelInvitation(id.toUInt(), code, channel, permission)
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM new.channel_invitations WHERE id = :id")
            .bind("id", id.toInt())
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM new.channel_invitations").execute()
    }

    override fun findById(id: UInt): ChannelInvitation? =
        handle
            .createQuery(
                """
                SELECT i.*,
                c.id as c_id,
                c.name,
                c.description,
                c.admin_id,
                c.visibility
                FROM new.channel_invitations i
                JOIN new.channels c ON i.channel_id = c.id 
                WHERE i.id = :id"""
            )
            .bind("id", id.toInt())
            .map { rs, _ -> mapRowToInvitation(rs) }
            .findOne()
            .orElse(null)


    override fun findAll(): List<ChannelInvitation> =
        handle
            .createQuery("""
                SELECT i.*, c.id as c_id, c.name, c.description, c.admin_id, c.visibility
                FROM new.channel_invitations i
                JOIN new.channels c ON i.channel_id = c.id 
                """
            )
            .map { rs, _ -> mapRowToInvitation(rs) }
            .list()

    override fun save(entity: ChannelInvitation) {
        handle
            .createUpdate(
                """
                UPDATE new.channel_invitations
                SET code = :code, channel_id = :channel_id, permission = :permission
                WHERE id = :id
                """,
            ).bind("id", entity.id.toInt())
            .bind("code", entity.code.toString())
            .bind("channel_id", entity.channel.id.toInt())
            .bind("permission", entity.permission.name)
            .execute()
    }

    private fun mapRowToInvitation(rs: ResultSet): ChannelInvitation {
        val channel = Channel(
            id = rs.getInt("channel_id").toUInt(),
            name = rs.getString("name"),
            adminID = rs.getInt("admin_id").toUInt(),
            description = rs.getString("description"),
            visibility = Visibility.valueOf(rs.getString("visibility")),
        )
        return ChannelInvitation(
            id = rs.getInt("id").toUInt(),
            code = UUID.fromString(rs.getString("code")),
            channel = channel,
            permission = Permission.valueOf(rs.getString("permission")),
        )
    }
}