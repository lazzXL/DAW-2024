import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertIs


class RegisterInvitationServicesTest {

    companion object {
        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()

        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )

        private fun cleanup(trxManager: TransactionManager) {
            trxManager.run {
                repoMessage.clear()
                repoParticipant.clear()
                repoChannelInvitation.clear()
                repoChannel.clear()
                repoRegisterInvitation.clear()
                repoUser.clear()
            }
        }

    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a register invitation`(trxManager: TransactionManager){
        val registerInvitationServices = RegisterInvitationServices(trxManager)
        val invitation = registerInvitationServices.createInvitation()
        assertIs<Success<RegisterInvitation>>(invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a register invitation`(trxManager: TransactionManager){
        val registerInvitationServices = RegisterInvitationServices(trxManager)
        val invitation = registerInvitationServices.createInvitation()
        assertIs<Success<RegisterInvitation>>(invitation)
        val delete = registerInvitationServices.deleteInvitation(invitation.value.code)
        assertIs<Success<Unit>>(delete)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to delete a register invitation that does not exist`(trxManager: TransactionManager){
        val registerInvitationServices = RegisterInvitationServices(trxManager)
        val delete = registerInvitationServices.deleteInvitation(UUID.randomUUID())
        assertIs<Failure<RegisterInvitationError.InvitationNotFound>>(delete)
    }



}