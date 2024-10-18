import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.ChannelController
import pt.isel.http_api.RegisterInvitationController
import pt.isel.http_api.UserController
import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.RegistrationInput
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ChannelControllerTests {


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
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )

        private fun cleanup(trxManager: TransactionManager) {
            trxManager.run {
                repoChannel.clear()
            }
        }

        private fun createChannelService(
            trxManager: TransactionManager,
        ) = ChannelServices(
            trxManager
        )

        private fun createUserService(
            trxManager: TransactionManager,
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = UserServices(
            trxManager,
            UsersDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UsersDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser,
                ),
            ),
            testClock,
        )

        }
    /*
    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create a channel`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val adminId = 1u
        val user = createUserService(trxManager, TestClock())

        val controllerUser = UserController(user)
        val registerInvitation = RegisterInvitationController(RegisterInvitationServices(trxManager))

        val invitation = registerInvitation.createInvitation()

        controllerUser.register(RegistrationInput(invitation.body!!.code, defaultName, defaultEmail, defaultPassword))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, adminId.toInt(), true)

        val response = controllerChannel.createChannel(createInput)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
    }
     */



}