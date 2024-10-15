package pt.isel

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RepositoryMessageJdbiTests : RepositoryJdbiTests() {

    private var participantId : UInt = 0u

    @BeforeEach
    fun createChannelAndParticipant() {
        runWithHandle { handle: Handle ->
            val user = RepositoryUserJdbi(handle).createUser("Alice",Email("alice99@email.com"),PasswordValidationInfo(newTokenValidationData()))
            val user2 = RepositoryUserJdbi(handle).createUser("Charles",Email("charles99@email.com"),PasswordValidationInfo(newTokenValidationData()))
            val channel = RepositoryChannelJdbi(handle).createChannel("Channel 1", "Channel 1 description", user.id, Visibility.PUBLIC)
            val participant  = RepositoryParticipantJdbi(handle).createParticipant(user2,channel,Permission.READ_WRITE)
            participantId = participant.id
        }
    }

    @Test
    fun `test create message and find it`() =
        runWithHandle { handle ->
            val repoMessages = RepositoryMessageJdbi(handle)
            repoMessages.sendMessage(
                "Hello!",
                LocalDateTime.now(),
                participantId
            )
            val users = repoMessages.findAll()
            assertEquals(1, users.size)
        }

    @Test
    fun `test create message and find it by id`() =
        runWithHandle { handle ->
            val repoMessages = RepositoryMessageJdbi(handle)
            val messageCreated =repoMessages.sendMessage(
                "Hello!",
                LocalDateTime.now().withNano((LocalDateTime.now().nano / 1000) * 1000),
                participantId
            )
            val message = repoMessages.findById(messageCreated.id)
            assertNotNull(message)
            assertEquals(messageCreated, message)
        }

    @Test
    fun `test modify message and save it`() =
        runWithHandle { handle ->
            val repoMessages = RepositoryMessageJdbi(handle)
            val messageCreated = repoMessages.sendMessage(
                "Hello!",
                LocalDateTime.now().withNano((LocalDateTime.now().nano / 1000) * 1000),
                participantId
            )
            val modifiedMessage = messageCreated.copy(content = "Hello channel!")
            repoMessages.save(modifiedMessage)
            val message = repoMessages.findById(messageCreated.id)
            assertNotNull(message)
            assertEquals(modifiedMessage, message)
        }

    @Test
    fun `test delete message`() =
        runWithHandle { handle ->
            val repoMessages = RepositoryMessageJdbi(handle)
            val messageCreated = repoMessages.sendMessage(
                "Hello!",
                LocalDateTime.now().withNano((LocalDateTime.now().nano / 1000) * 1000),
                participantId
            )
            val messages = repoMessages.findAll()
            assertEquals(1, messages.size)

            repoMessages.deleteById(messageCreated.id)
            val messagesAfterDelete = repoMessages.findAll()
            assertEquals(0, messagesAfterDelete.size)
        }

}