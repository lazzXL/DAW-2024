package pt.isel.http_api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.CreateInvitationInput
import java.util.stream.Stream
import kotlin.test.assertIs

class ChannelInvitationControllerTests {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )
    }

    private fun createChannel (trxManager: TransactionManager, authUser : AuthenticatedUser): Channel {
        val channel = ChannelController(createChannelService(trxManager))
        val channelInput = CreateChannelInput("channelName", "channelDescription", false)
        return channel.createChannel(channelInput, authUser).body!!
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create an invitation`(trxManager: TransactionManager) {
        val controllerChannelInvitation = ChannelInvitationController(createChannelInvitationService(trxManager))

        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))
        val channel = createChannel(trxManager, authUser)

        val invitationInput = CreateInvitationInput(channel.id, Permission.READ_WRITE)

        controllerChannelInvitation.createInvitation(invitationInput, authUser).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<ChannelInvitation>(resp.body)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test delete an invitation`(trxManager: TransactionManager) {
        val controllerChannelInvitation = ChannelInvitationController(createChannelInvitationService(trxManager))

        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))
        val channel = createChannel(trxManager, authUser)

        val invitationInput = CreateInvitationInput(channel.id, Permission.READ_WRITE)

        val invitation = controllerChannelInvitation.createInvitation(invitationInput, authUser).body!!
        controllerChannelInvitation.leaveChannel(invitation.id, authUser).let { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
        }
    }

}