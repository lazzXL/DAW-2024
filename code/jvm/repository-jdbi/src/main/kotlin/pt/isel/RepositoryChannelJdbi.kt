package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet

class RepositoryChannelJdbi(
    private val handle: Handle,
) : RepositoryChannel {

    private val defaultSkip = 0
    private val defaultLimit = 20

    override fun findById(id: UInt): Channel? =
        handle
            .createQuery(
                """
            SELECT c.* FROM new.channels c
            WHERE c.id = :id
            """,
            ).bind("id", id.toInt())
            .map { rs, _ -> mapRowToChannel(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<Channel> =
        handle
            .createQuery(
                """
                SELECT c.* FROM new.channels c
                """,
            ).map { rs, _ -> mapRowToChannel(rs) }
            .list()



    override fun save(entity: Channel) {
        handle
            .createUpdate(
                """
            UPDATE new.channels 
            SET name = :name, description = :description, admin_id = :admin_id, visibility = :visibility
            WHERE id = :id
            """,
            ).bind("id", entity.id.toInt())
            .bind("name", entity.name)
            .bind("description", entity.description)
            .bind("admin_id", entity.adminID.toInt())
            .bind("visibility", entity.visibility.name)
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM new.channels WHERE id = :id")
            .bind("id", id.toInt())
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
                INSERT INTO new.channels (name, description, admin_id, visibility) 
                VALUES (:name, :description, :admin_id, :visibility)
                """,
                ).bind("name", name)
                .bind("description", description)
                .bind("admin_id", adminID.toInt())
                .bind("visibility", visibility.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return Channel(id.toUInt(), name, adminID, description, visibility)
    }

    override fun findByName(name: String): Channel? =
        handle
            .createQuery(
            """
                    SELECT c.* FROM new.channels c
                    WHERE c.name = :name
                    """,
            ).bind("name", name)
            .map { rs, _ -> mapRowToChannel(rs) }
            .findOne()
            .orElse(null)

    override fun findAllByUser(userID: UInt,name:String?,limit: Int?,skip: Int?): List<Channel> =
        handle
            .createQuery(
            """
                    SELECT c.* FROM new.channels c
                    JOIN new.participants p ON c.id = p.channel_id
                    WHERE p.user_id = :user_id
                    AND p.is_active = TRUE
                    AND (:name IS NULL OR c.name LIKE :name)
                    LIMIT :limit OFFSET :skip
                    """,
            ).bind("user_id", userID.toInt())
            .bind("name", name?.let { "%$it%" })
            .bind("limit", limit ?: defaultLimit)
            .bind("skip", skip ?: defaultSkip)
            .map { rs, _ -> mapRowToChannel(rs) }
            .list()

    override fun getPublicChannels(name: String?, limit: Int?, skip: Int?): List<Channel> =
        handle
            .createQuery(
                """
                SELECT c.* FROM new.channels c
                WHERE c.visibility = :visibility
                AND (:name IS NULL OR c.name LIKE :name)
                LIMIT :limit OFFSET :skip
                """,
            ).bind("visibility", "PUBLIC")
            .bind("name", name?.let { "%$it%" })
            .bind("limit", limit ?: defaultLimit)
            .bind("skip", skip ?: defaultSkip)
            .map { rs, _ -> mapRowToChannel(rs) }
            .list()


    override fun clear() {
        handle.createUpdate("DELETE FROM new.channels").execute()
    }

    // TODO: OTHER POSSIBLE FUTURE IMPLEMENTATIONS: findAllChannelsByAdmin and findPublicChannel(s)ByName

    /**
     * Auxiliary function to map the DB table 'Channel' to domain class 'Channel'
     */
    private fun mapRowToChannel(rs: ResultSet): Channel {
        return Channel(
            id = rs.getInt("id").toUInt(),
            name = rs.getString("name"),
            description = rs.getString("description"),
            adminID = rs.getInt("admin_id").toUInt(),
            visibility = Visibility.valueOf(rs.getString("visibility")),
        )
    }
}
