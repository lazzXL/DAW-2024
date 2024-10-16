package pt.isel

import kotlinx.datetime.Clock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class RepositoryUserJdbiTests : RepositoryJdbiTests() {

    @Test
    fun `test create user and find it`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            repoUsers.createUser(
                "Alice",
                Email("alice99@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )
            val users = repoUsers.findAll()
            assertEquals(1, users.size)
        }

    @Test
    fun `test create multiple users and find them`() =
        runWithHandle { handle ->
            val numberOfUsersCreated = 5
            val repoUsers = RepositoryUserJdbi(handle)
            repeat(numberOfUsersCreated){
                repoUsers.createUser(
                    "Alice$it",
                    Email("alice99$it@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            }
            val users = repoUsers.findAll()
            assertEquals(numberOfUsersCreated, users.size)
            repeat(numberOfUsersCreated) { idx ->
                assertTrue(users.any{ it.name == "Alice${idx}"})
            }
        }

    @Test
    fun `test create user and find it by id`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice =
                repoUsers.createUser(
                    "Alice",
                    Email("alice99@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            val user = repoUsers.findById(alice.id)
            assertNotNull(user)
            assertEquals(user, alice)
        }

    @Test
    fun `test modify user and save it`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice =
                repoUsers.createUser(
                    "Alice",
                    Email("alice99@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            val modifiedAlice = alice.copy(name = "AliceSmith")
            repoUsers.save(modifiedAlice)
            val user = repoUsers.findById(alice.id)
            assertNotNull(user)
            assertEquals(modifiedAlice, user)
        }

    @Test
    fun `test delete user`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice =
                repoUsers.createUser(
                    "Alice",
                    Email("alice99@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            val users = repoUsers.findAll()
            assertEquals(1, users.size)
            repoUsers.deleteById(alice.id)
            val usersAfterDelete = repoUsers.findAll()
            assertEquals(0, usersAfterDelete.size)
        }

    @Test
    fun `test clear users`() =
        runWithHandle { handle ->
            val numOfUsersCreated = 5
            val repoUsers = RepositoryUserJdbi(handle)
            repeat(numOfUsersCreated){
                repoUsers.createUser(
                    "Alice$it",
                    Email("alice99$it@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            }
            val users = repoUsers.findAll()
            assertEquals(numOfUsersCreated, users.size)
            repoUsers.clear()
            val usersAfterClear = repoUsers.findAll()
            assertEquals(0, usersAfterClear.size)
        }

    @Test
    fun `test create user and find it by email`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val email = Email("alice99@example.com")
            val alice =
                repoUsers.createUser(
                    "Alice",
                    email,
                    PasswordValidationInfo(newTokenValidationData())
                )
            val user = repoUsers.findByEmail(email)
            assertNotNull(user)
            assertEquals(user, alice)
        }

    @Test
    fun `test create user and find it by username`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val username = "Alice"
            val alice =
                repoUsers.createUser(
                    username,
                    Email("alice99@example.com"),
                    PasswordValidationInfo(newTokenValidationData())
                )
            val user = repoUsers.findByName(username)
            assertNotNull(user)
            assertEquals(user, alice)
        }

    @Test
    fun `test create token and retrieve it`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice = repoUsers.createUser(
                "Alice",
                Email("alice91@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )
            val tokenInfo = TokenValidationInfo(newTokenValidationData())
            val token = Token(tokenInfo, alice.id, Clock.System.now(), Clock.System.now())

            repoUsers.createToken(token, maxTokens = 5)

            val tokenFromDb = repoUsers.getTokenByTokenValidationInfo(tokenInfo)
            assertNotNull(tokenFromDb)
            val (user, retrievedToken) = tokenFromDb
            assertEquals(alice, user)
            assertEquals(token.tokenValidationInfo, retrievedToken.tokenValidationInfo)
        }

    @Test
    fun `test create multiple tokens and limit to max tokens`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice = repoUsers.createUser(
                "Alice",
                Email("alice99@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )

            val maxTokens = 3
            repeat(maxTokens + 2) {
                val tokenInfo = TokenValidationInfo(newTokenValidationData())
                val token = Token(tokenInfo, alice.id, Clock.System.now(), Clock.System.now())
                repoUsers.createToken(token, maxTokens)
            }

            // Ensure only maxTokens remain
            val tokensInDb = handle.createQuery("SELECT COUNT(*) FROM dbo.tokens WHERE user_id = :user_id")
                .bind("user_id", alice.id.toInt())
                .mapTo(Int::class.java)
                .one()

            assertEquals(maxTokens, tokensInDb)
        }

    @Test
    fun `test update token last used`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice = repoUsers.createUser(
                "Alice",
                Email("alice99@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )

            val tokenInfo = TokenValidationInfo(newTokenValidationData())
            val createdAt = Clock.System.now()
            val token = Token(tokenInfo, alice.id, createdAt, createdAt)
            repoUsers.createToken(token, maxTokens = 5)

            // Simulate later time
            val newLastUsedAt = createdAt.plus(1.toDuration(DurationUnit.HOURS))
            repoUsers.updateTokenLastUsed(token, newLastUsedAt)

            val updatedToken = repoUsers.getTokenByTokenValidationInfo(tokenInfo)
            assertNotNull(updatedToken)
            val (_, retrievedToken) = updatedToken
            assertEquals(newLastUsedAt.epochSeconds, retrievedToken.lastUsedAt.epochSeconds)
        }

    @Test
    fun `test remove token by validation info`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val alice = repoUsers.createUser(
                "Alice",
                Email("alice99@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )

            val tokenInfo = TokenValidationInfo(newTokenValidationData())
            val token = Token(tokenInfo, alice.id, Clock.System.now(), Clock.System.now())
            repoUsers.createToken(token, maxTokens = 5)

            val tokenFromDb = repoUsers.getTokenByTokenValidationInfo(tokenInfo)
            assertNotNull(tokenFromDb)

            val rowsDeleted = repoUsers.removeTokenByValidationInfo(tokenInfo)
            assertEquals(1, rowsDeleted)

            val tokenAfterDelete = repoUsers.getTokenByTokenValidationInfo(tokenInfo)
            assertNull(tokenAfterDelete)
        }

    @Test
    fun `test find user by token`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)

            val alice = repoUsers.createUser(
                "Alice",
                Email("alice99@example.com"),
                PasswordValidationInfo(newTokenValidationData())
            )

            val tokenInfo = TokenValidationInfo(newTokenValidationData())
            val token = Token(tokenInfo, alice.id, Clock.System.now(), Clock.System.now())
            repoUsers.createToken(token, maxTokens = 5)

            val userByToken = repoUsers.findByToken(tokenInfo.validationInfo)

            assertNotNull(userByToken)
            assertEquals(alice, userByToken)
        }

    @Test
    fun `test find user by invalid token returns null`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)

            val invalidToken = "non_existing_token"
            val userByToken = repoUsers.findByToken(invalidToken)

            assertNull(userByToken)
        }
}