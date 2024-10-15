package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.util.*

class RepositoryRegisterInvitationJdbi(
    private val handle: Handle,
):RepositoryRegisterInvitation {

    override fun findByCode(code: UUID): RegisterInvitation? =
        handle
            .createQuery(
                """
            SELECT i.* FROM dbo.register_invitations i
            JOIN dbo.channels c ON i.channel_id = c.id 
            WHERE c.code = :code
            """,
            ).bind("code", code.toString())
            .map { rs, _ -> mapRowToInvitation(rs) }
            .findOne()
            .orElse(null)


    override fun createInvitation(code: UUID): RegisterInvitation {
        val id =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.register_invitations (code) 
                VALUES (:code)
                """,
                ).bind("code", code.toString())
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return RegisterInvitation(id.toUInt(), code)
    }

    override fun findById(id: UInt): RegisterInvitation? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<RegisterInvitation> {
        TODO("Not yet implemented")
    }

    override fun save(entity: RegisterInvitation) {
        TODO("Not yet implemented")
    }


    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.register_invitations WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.register_invitations").execute()
    }

    private fun mapRowToInvitation(rs: ResultSet): RegisterInvitation =
        RegisterInvitation(
            id = rs.getInt("id").toUInt(),
            code = UUID.fromString(rs.getString("code")),
        )


}