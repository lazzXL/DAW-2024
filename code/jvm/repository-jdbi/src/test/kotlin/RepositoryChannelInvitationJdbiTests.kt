package pt.isel

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RepositoryChannelInvitationJdbiTests : RepositoryJdbiTests(){

    private val code = UUID.randomUUID()
    private val permission = Permission.READ_WRITE
    private lateinit var channel : Channel

    @BeforeEach
    fun addAdminUser() {
        runWithHandle { handle: Handle ->
            val user = RepositoryUserJdbi(handle).createUser("Alice",Email("alice99@email.com"),UUID.randomUUID(),"password")
            channel = RepositoryChannelJdbi(handle).createChannel("Channel","Description",user.id,Visibility.PUBLIC)
        }
    }

    @Test
    fun `test create an invitation and find it by code`() =
        runWithHandle { handle: Handle ->
            val repoChannelInvitation = RepositoryChannelInvitationJdbi(handle)
            val createdInvitation = repoChannelInvitation.createInvitation(
                code,
                permission,
                channel
            )
            val invitation = repoChannelInvitation.findByCode(createdInvitation.code)
            assertNotNull(invitation)
            assertEquals(createdInvitation,invitation)
        }

    @Test
    fun `test create an invitation and delete it`() =
        runWithHandle { handle: Handle ->
            val repoChannelInvitation = RepositoryChannelInvitationJdbi(handle)
            val createdInvitation = repoChannelInvitation.createInvitation(
                code,
                permission,
                channel
            )
            repoChannelInvitation.deleteById(createdInvitation.id)
            val invitationsAfterDelete = repoChannelInvitation.findAll()
            assertEquals(0,invitationsAfterDelete.size)
        }

    @Test
    fun `test create an invitation and find it by id`() =
        runWithHandle { handle: Handle ->
            val repoChannelInvitation = RepositoryChannelInvitationJdbi(handle)
            val createdInvitation = repoChannelInvitation.createInvitation(
                code,
                permission,
                channel
            )
            val invitation = repoChannelInvitation.findById(createdInvitation.id)
            assertNotNull(invitation)
            assertEquals(createdInvitation,invitation)
        }

    @Test
    fun `test update an invitation` () =
        runWithHandle { handle: Handle ->
            val repoChannelInvitation = RepositoryChannelInvitationJdbi(handle)
            val createdInvitation = repoChannelInvitation.createInvitation(
                code,
                permission,
                channel
            )
            val updatedInvitation = createdInvitation.copy(permission = Permission.READ_ONLY)
            repoChannelInvitation.save(updatedInvitation)
            val invitation = repoChannelInvitation.findById(createdInvitation.id)
            assertNotNull(invitation)
            assertEquals(Permission.READ_ONLY, invitation.permission)
        }
}