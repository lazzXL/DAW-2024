package pt.isel

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class RepositoryUserJdbiTests : RepositoryJdbiTests() {
    //TODO: Tests for token functions
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
}