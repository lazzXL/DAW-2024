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

    override fun findById(id: UInt): RegisterInvitation? =
        handle
            .createQuery("""
                SELECT * FROM dbo.register_invitations 
                WHERE id = :id
                """)
            .bind("id", id.toInt())
            .map { rs, _ -> mapRowToInvitation(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<RegisterInvitation> =
        handle
            .createQuery("SELECT * FROM dbo.register_invitations")
            .map { rs, _ -> mapRowToInvitation(rs) }
            .list()

    override fun save(entity: RegisterInvitation) {
        handle
            .createUpdate(
                """
                UPDATE dbo.register_invitations
                SET code = :code
                WHERE id = :id
                """,
            ).bind("id", entity.id.toInt())
            .bind("user_id", entity.code.toString())
            .execute()
    }


    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.register_invitations WHERE id = :id")
            .bind("id", id.toInt())
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