package pt.isel.http_api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import pt.isel.AuthenticatedUser
import pt.isel.RegisterInvitation
import pt.isel.TransactionManager
import pt.isel.TransactionManagerJdbi
import java.util.stream.Stream
import kotlin.test.assertIs

class RegisterInvitationControllerTests {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create an invitation`(trxManager: TransactionManager) {
        val controllerRegisterInvitation = RegisterInvitationController(createRegisterInvitationService(trxManager))

        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))
        controllerRegisterInvitation.createInvitation(authUser).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<RegisterInvitation>(resp.body)
        }
    }
    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test delete an invitation`(trxManager: TransactionManager) {
        val controllerRegisterInvitation = RegisterInvitationController(createRegisterInvitationService(trxManager))

        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))
        val invitation = controllerRegisterInvitation.createInvitation(authUser).body!!
        controllerRegisterInvitation.deleteInvitation(invitation.code, authUser).let { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
        }
    }

}