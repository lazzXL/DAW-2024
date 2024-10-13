package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet

class RepositoryChannelJdbi(
    private val handle: Handle,
) : RepositoryChannel {

    override fun findById(id: UInt): Channel? =
        handle
            .createQuery(
                """
            SELECT c.* FROM dbo.channels c
            WHERE c.id = :id
            """,
            ).bind("id", id)
            .map { rs, _ -> mapRowToChannel(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<Channel> =
        handle
            .createQuery(
                """
                SELECT c.* FROM dbo.channels c
                WHERE c.visibility = :visibility
                """,
            ).bind("visibility", "Public")
            .map { rs, _ -> mapRowToChannel(rs) }
            .list()

    override fun save(entity: Channel) {
        handle
            .createUpdate(
                """
            UPDATE dbo.channels 
            SET name = :name, description = :description, admin_id = :admin_id, visibility = :visibility
            WHERE id = :id
            """,
            ).bind("id", entity.id)
            .bind("name", entity.name)
            .bind("description", entity.description)
            .bind("admin_id", entity.adminID)
            .bind("visibility", entity.visibility.name)
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.channels WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun createChannel(
        name: String,
        description: String,
        adminID: UInt,
        visibility: Visibility
    ): Channel {
        val id =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.channels (name, description, admin_id, visibility) 
                VALUES (:name, :description, :admin_id, :visibility)
                """,
                ).bind("name", name)
                .bind("description", description)
                .bind("admin_id", adminID)
                .bind("visibility", visibility.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(UInt::class.java)
                .one()

        return Channel(id, name, adminID, description, visibility)
    }

    override fun findByName(name: String): Channel? =
        handle
            .createQuery(
            """
                    SELECT c.* FROM dbo.channels c
                    WHERE c.name = :name
                    """,
            ).bind("name", name)
            .map { rs, _ -> mapRowToChannel(rs) }
            .findOne()
            .orElse(null)

    override fun findAllByUser(userID: UInt): List<Channel> =
        handle
            .createQuery(
            """
                    SELECT c.* FROM dbo.channels c
                    JOIN dbo.participants p ON c.id = p.channel_id
                    WHERE p.user_id = :user_id
                    """,
            ).bind("user_id", userID)
            .map { rs, _ -> mapRowToChannel(rs) }
            .list()

    override fun getPublicChannels(): List<Channel> {
        TODO("Not yet implemented")
    }

    /*override fun clear() {
        handle.createUpdate("DELETE FROM dbo.channels").execute()
    }*/

    // TODO: OTHER POSSIBLE FUTURE IMPLEMENTATIONS: findAllChannelsByAdmin and findPublicChannel(s)ByName

    /**
     * Auxiliary function to map the DB table 'Channel' to domain class 'Channel'
     */
    private fun mapRowToChannel(rs: ResultSet): Channel {
        return Channel(
            id = rs.getInt("id").toUInt(),
            name = rs.getString("name"),
            description = rs.getString("description"),
            adminID = rs.getInt("admin").toUInt(),
            visibility = Visibility.valueOf(rs.getString("visibility")),
        )
    }
}
