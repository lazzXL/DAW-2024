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
            .bind("id", id.toInt())
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
            SET name = :name, email = :email , password = :password
            WHERE id = :id
            """,
            ).bind("name", entity.name)
            .bind("email", entity.email.toString())
            .bind("password", entity.password)
            .bind("id", entity.id.toInt())
            .execute()
    }

    override fun deleteById(id: UInt) {
        handle
            .createUpdate("DELETE FROM dbo.users WHERE id = :id")
            .bind("id", id.toInt())
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.users").execute()
    }

    override fun createUser(
        name: String,
        email: Email,
        token: UUID,
        password: PasswordValidationInfo
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
                .bind("password", password.validationInfo)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return User(id.toUInt(), name, email, password)
    }

    override fun createToken(token: Token): Token {
        TODO("Not yet implemented")
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Token? {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: Email): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE email = :email")
            .bind("email", email.toString())
            .map { rs, _ -> mapRowToUser(rs) }
            .findOne()
            .orElse(null)

    override fun findByName(name: String): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE name = :name")
            .bind("name", name)
            .map { rs, _ -> mapRowToUser(rs) }
            .findOne()
            .orElse(null)


    private fun mapRowToUser(rs: ResultSet): User {
        return User(
            id = rs.getInt("id").toUInt(),
            name = rs.getString("name"),
            email = Email(rs.getString("email")),
            password = PasswordValidationInfo(rs.getString("password")),
        )
    }

}
