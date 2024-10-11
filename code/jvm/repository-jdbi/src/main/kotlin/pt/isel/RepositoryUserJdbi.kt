package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.util.*

class RepositoryUserJdbi(
    private val handle: Handle,
) : RepositoryUser {

    override fun findById(id: UInt): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .map { rs, _ -> mapRowToUser(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<User> =
        handle
            .createQuery("SELECT * FROM dbo.users")
            .map { rs, _ -> mapRowToUser(rs) }
            .list()

    override fun save(entity: User) {
        handle
            .createUpdate(
                """
            UPDATE dbo.users 
            SET name = :name, email = :email , token = :token, password = :password
            WHERE id = :id
            """,
            ).bind("name", entity.name)
            .bind("email", entity.email.toString())
            .bind("token", entity.token.toString())
            .bind("password", entity.token.toString())
            .bind("id", entity.id)
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    /*override fun clear() {
        handle.createUpdate("DELETE FROM dbo.users").execute()
    }*/

    override fun createUser(
        name: String,
        email: Email,
        token: UUID,
        password: String
    ): User {
        val id =
            handle
                .createUpdate(
                    """
            INSERT INTO dbo.users (name, email, token, password) 
            VALUES (:name, :email, :token, :password)
            RETURNING id
            """,
                ).bind("name", name)
                .bind("email", email.toString())
                .bind("token", token.toString())
                .bind("password", password)
                .executeAndReturnGeneratedKeys()
                .mapTo(UInt::class.java)
                .one()

        return User(id, token, name, email, password)
    }

    override fun findByEmail(email: Email): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE email = :email")
            .bind("email", email.toString())
            .map { rs, _ -> mapRowToUser(rs) }
            .findOne()
            .orElse(null)

    private fun mapRowToUser(rs: ResultSet): User {
        return User(
            id = rs.getInt("id").toUInt(),
            name = rs.getString("name"),
            email = Email(rs.getString("email")),
            token = UUID.fromString(rs.getString("token")),
            password = rs.getString("password"),
        )
    }

}
