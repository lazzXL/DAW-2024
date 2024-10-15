package pt.isel

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositoryParticipantJdbiTests : RepositoryJdbiTests() {

    private val participantPermission = Permission.READ_WRITE
    private lateinit var channel : Channel
    private lateinit var user : User

    @BeforeEach
    fun createUserAndChannel() {
        runWithHandle { handle: Handle ->
            val admin = RepositoryUserJdbi(handle).createUser("Alice",Email("alice99@email.com"),PasswordValidationInfo(newTokenValidationData()))
            user = RepositoryUserJdbi(handle).createUser("BobMarley",Email("bob99@email.com"),PasswordValidationInfo(newTokenValidationData()))
            channel = RepositoryChannelJdbi(handle).createChannel("Channel","Description",admin.id,Visibility.PUBLIC)
        }
    }

    @Test
    fun `test create participant and find it by id`() =
        runWithHandle { handle ->
            val repoParticipants = RepositoryParticipantJdbi(handle)
            val createdParticipant = repoParticipants.createParticipant(
                user,
                channel,
                participantPermission
            )
            val participant = repoParticipants.findById(createdParticipant.id)
            assertNotNull(participant)
            assertEquals(participant, createdParticipant)
        }

    @Test
    fun `test create multiple participants and find them`() =
        runWithHandle { handle ->
            val repoUsers = RepositoryUserJdbi(handle)
            val otherUser = repoUsers.createUser("Charles",Email("charles@email.com"), PasswordValidationInfo(newTokenValidationData()))
            val repoParticipants = RepositoryParticipantJdbi(handle)
            repoParticipants.createParticipant(
                user,
                channel,
                participantPermission
            )
            repoParticipants.createParticipant(
                otherUser,
                channel,
                Permission.READ_ONLY
            )
            val participant = repoParticipants.findAll()
            assertEquals(2, participant.size)
        }

    @Test
    fun `test modify participant and save it`() =
        runWithHandle { handle ->
            val repoParticipants = RepositoryParticipantJdbi(handle)
            val participantCreated =
                repoParticipants.createParticipant(
                    user,
                    channel,
                    participantPermission,
                )
            val modifiedParticipant = participantCreated.copy(permission = Permission.READ_ONLY)
            repoParticipants.save(modifiedParticipant)
            val participant = repoParticipants.findById(participantCreated.id)
            assertNotNull(participant)
            assertEquals(modifiedParticipant, participant)
        }

    @Test
    fun `test delete participant`() =
        runWithHandle { handle ->
            val repoParticipants = RepositoryParticipantJdbi(handle)
            val participantCreated =
                repoParticipants.createParticipant(
                    user,
                    channel,
                    participantPermission,
                )
            val participants = repoParticipants.findAll()
            assertEquals(1, participants.size)

            repoParticipants.deleteById(participantCreated.id)
            val participantsAfterDelete = repoParticipants.findAll()
            assertEquals(0, participantsAfterDelete.size)
        }

    @Test
    fun `test if user is participant`() =
        runWithHandle { handle ->
            val repoParticipants = RepositoryParticipantJdbi(handle)
            val userNotInChannel = RepositoryUserJdbi(handle).createUser("David", Email("david@email.com"),PasswordValidationInfo(newTokenValidationData()))
            repoParticipants.createParticipant(
                user,
                channel,
                participantPermission,
            )
            assertTrue(repoParticipants.isParticipant(channel.id,user.id))
            assertFalse(repoParticipants.isParticipant(channel.id,userNotInChannel.id))
        }

}