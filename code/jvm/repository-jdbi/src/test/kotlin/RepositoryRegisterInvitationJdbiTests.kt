package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull

class RepositoryRegisterInvitationJdbiTests : RepositoryJdbiTests(){
    @Test
    fun `test create register invitation and find it by id`() {
        runWithHandle { handle ->
            val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)
            val code = UUID.randomUUID()
            val createdRegisterInvitation = repoRegisterInvitation.createInvitation(code)
            val registerInvitation = repoRegisterInvitation.findById(createdRegisterInvitation.id)
            assertNotNull(registerInvitation)
            assertEquals(registerInvitation.code, code)
        }
    }

    @Test
    fun `test create register invitation and find it by code`() {
        runWithHandle { handle ->
            val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)
            val code = UUID.randomUUID()
            val createdRegisterInvitation = repoRegisterInvitation.createInvitation(code)
            val registerInvitation = repoRegisterInvitation.findByCode(code)
            assertNotNull(registerInvitation)
            assertEquals(registerInvitation.id, createdRegisterInvitation.id)
        }
    }

    @Test
    fun `test create register invitation and find all register invitations`() {
        runWithHandle { handle ->
            val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)
            val code = UUID.randomUUID()
            repoRegisterInvitation.createInvitation(code)
            val registerInvitations = repoRegisterInvitation.findAll()
            assertEquals(1, registerInvitations.size)
        }
    }

    @Test
    fun `test create register invitation and save it`() {
        runWithHandle { handle ->
            val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)
            val code = UUID.randomUUID()
            val createdRegisterInvitation = repoRegisterInvitation.createInvitation(code)
            val newCode = UUID.randomUUID()
            val updatedRegisterInvitation = RegisterInvitation(createdRegisterInvitation.id, newCode)
            repoRegisterInvitation.save(updatedRegisterInvitation)
            val registerInvitation = repoRegisterInvitation.findById(createdRegisterInvitation.id)
            assertNotNull(registerInvitation)
            assertEquals(registerInvitation.code, newCode)
        }
    }

    @Test
    fun `test create register invitation and delete it`() {
        runWithHandle { handle ->
            val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)
            val code = UUID.randomUUID()
            val createdRegisterInvitation = repoRegisterInvitation.createInvitation(code)
            repoRegisterInvitation.deleteById(createdRegisterInvitation.id)
            val registerInvitation = repoRegisterInvitation.findById(createdRegisterInvitation.id)
            assertEquals(null, registerInvitation)
        }
    }
}