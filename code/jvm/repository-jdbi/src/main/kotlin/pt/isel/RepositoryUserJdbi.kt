package pt.isel

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.ResultSet


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
            .bind("password", entity.password.validationInfo)
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
        password: PasswordValidationInfo
    ): User {
        val id =
            handle
                .createUpdate(
                    """
                    INSERT INTO dbo.users (name, email, password) 
                    VALUES (:name, :email, :password)
                    RETURNING id
                    """,
                ).bind("name", name)
                .bind("email", email.toString())
                .bind("password", password.validationInfo)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return User(id.toUInt(), name, email, password)
    }

    override fun createToken(token: Token, maxTokens: Int) {
        handle
            .createUpdate(
                """
                DELETE FROM dbo.tokens 
                WHERE user_id = :user_id AND token_validation IN (
                    SELECT token_validation FROM dbo.tokens WHERE user_id = :user_id 
                    ORDER BY last_used_at DESC OFFSET :offset
                )
                """,
            ).bind("user_id", token.userId.toInt())
            .bind("offset", maxTokens - 1)
            .execute()

        handle
            .createUpdate(
                """
                INSERT INTO dbo.tokens(user_id, token_validation, created_at, last_used_at) 
                VALUES (:user_id, :token_validation, :created_at, :last_used_at)
                """,
            ).bind("user_id", token.userId.toInt())
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle
            .createQuery(
                """
                SELECT id, name, email, password, token_validation, created_at, last_used_at
                FROM dbo.users u 
                JOIN dbo.tokens t ON u.id = t.user_id
                WHERE token_validation = :validation_information
            """,
            ).bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken


    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle
            .createUpdate(
                """
                UPDATE dbo.tokens
                SET last_used_at = :last_used_at
                WHERE token_validation = :validation_information
                """,
            ).bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int =
        handle
            .createUpdate(
            """
                DELETE FROM dbo.tokens
                WHERE token_validation = :validation_information
                """,
            ).bind("validation_information", tokenValidationInfo.validationInfo)
            .execute()


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

    private data class UserAndTokenModel(
        val id: Int,
        val name: String,
        val email: String,
        val password: String,
        val tokenValidation: String,
        val createdAt: Long,
        val lastUsedAt: Long,
    ) {
        val userAndToken: Pair<User, Token>
            get() =
                Pair(
                    User(id.toUInt(), name, Email(email), PasswordValidationInfo(password)),
                    Token(
                        TokenValidationInfo(tokenValidation),
                        id.toUInt(),
                        Instant.fromEpochSeconds(createdAt),
                        Instant.fromEpochSeconds(lastUsedAt),
                    ),
                )
    }

}
