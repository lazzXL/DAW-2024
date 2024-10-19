package pt.isel.http_api


import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.model.*
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ParticipantControllerTests {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )
    }


    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test join channel by invite`(trxManager: TransactionManager) {
        val controllerUser = UserController(createUserService(trxManager, TestClock()))

        val user1 = controllerUser.register(RegistrationInput(defaultInvitation, defaultName, defaultEmail, defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals(defaultName, user.name)
            assertEquals(defaultEmail, user.email)
            user
        }
        val user2 = controllerUser.register(RegistrationInput(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Charles", Email("charles@email.com"), defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals("Charles", user.name)
            assertEquals(Email("charles@email.com"), user.email)
            user
        }

        val controllerChannel = ChannelController(createChannelService(trxManager))
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(user1, "00000000-0000-0000-0000-000000000000")
        val authUser2 = AuthenticatedUser(user2, "00000000-0000-0000-0000-000000000001")

        val createInput = CreateChannelInput(channelName, channelDescription, false)

        val response = controllerChannel.createChannel(createInput, authUser)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val channel = response.body as Channel

        val controllerChannelInvitation = ChannelInvitationController(createChannelInvitationService(trxManager))
        val inviteResponse = controllerChannelInvitation.createInvitation(CreateInvitationInput(channel.id,Permission.READ_WRITE), authUser)
        val invite = inviteResponse.body as ChannelInvitation
        val controllerParticipant = ParticipantController(createParticipantService(trxManager))
        controllerParticipant.joinChannelByInvite(JoinChannelViaInviteInput(invite.code),authUser2).let { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test join public channel`(trxManager: TransactionManager) {
        val controllerUser = UserController(createUserService(trxManager, TestClock()))

        val user1 = controllerUser.register(RegistrationInput(defaultInvitation, defaultName, defaultEmail, defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals(defaultName, user.name)
            assertEquals(defaultEmail, user.email)
            user
        }
        val user2 = controllerUser.register(RegistrationInput(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Charles", Email("charles@email.com"), defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals("Charles", user.name)
            assertEquals(Email("charles@email.com"), user.email)
            user
        }

        val controllerChannel = ChannelController(createChannelService(trxManager))
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(user1, "00000000-0000-0000-0000-000000000000")
        val authUser2 = AuthenticatedUser(user2, "00000000-0000-0000-0000-000000000001")

        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val channel = response.body as Channel


        val controllerParticipant = ParticipantController(createParticipantService(trxManager))
        controllerParticipant.joinPublicChannel(JoinPublicChannelInput(channel.id),authUser2).let { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test leave channel`(trxManager: TransactionManager) {
        val controllerUser = UserController(createUserService(trxManager, TestClock()))

        val user1 = controllerUser.register(RegistrationInput(defaultInvitation, defaultName, defaultEmail, defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals(defaultName, user.name)
            assertEquals(defaultEmail, user.email)
            user
        }
        val user2 = controllerUser.register(RegistrationInput(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Charles", Email("charles@email.com"), defaultPassword)).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<User>(resp.body)
            val user = resp.body as User
            assertEquals("Charles", user.name)
            assertEquals(Email("charles@email.com"), user.email)
            user
        }

        val controllerChannel = ChannelController(createChannelService(trxManager))
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(user1, "00000000-0000-0000-0000-000000000000")
        val authUser2 = AuthenticatedUser(user2, "00000000-0000-0000-0000-000000000001")

        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val channel = response.body as Channel


        val controllerParticipant = ParticipantController(createParticipantService(trxManager))
        controllerParticipant.joinPublicChannel(JoinPublicChannelInput(channel.id),authUser2).let { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
        }

        controllerParticipant.leaveChannel(channel.id,authUser2).let {
            assertEquals(HttpStatus.OK, it.statusCode)
        }
    }

}