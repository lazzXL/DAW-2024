import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UserServicesTest {

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
                repoRegisterInvitation.createInvitation(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                repoRegisterInvitation.createInvitation(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            }
        }
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

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `registration, login and find by name test`(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Success<User>>(registration)
        val login = userServices.login("Antonio", "Password3")
        assertIs<Success<TokenExternalInfo>>(login)
        val user = userServices.findByName("Antonio")
        assertIs<Success<User>>(user)
        assertEquals(user.value.name, "Antonio")
        assertEquals(user.value.email, Email("Antonio@email.com"))
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to find a user with an incorrect name `(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Success<User>>(registration)
        val user = userServices.findByName("Paulo")
        assertIs<Failure<UserError.UserNotFound>>(user)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to login with an incorrect passoword `(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Success<User>>(registration)
        val login = userServices.login("Antonio", "12345")
        assertIs<Failure<UserError.IncorrectPassword>>(login)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to register with an invalid code`(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000099"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Failure<UserError.InvitationDoesNotExist>>(registration)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to register with an email that is already in use `(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Success<User>>(registration)
        val registration2 = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000001"),
            Email("Antonio@email.com"),
            "Antonio Silva",
            "Password2")
        assertIs<Failure<UserError.EmailAlreadyExists>>(registration2)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to register with a name that is already in use `(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio Silva",
            "Password3")
        assertIs<Success<User>>(registration)
        val registration2 = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000001"),
            Email("AntonioSilva@email.com"),
            "Antonio Silva",
            "Password2")
        assertIs<Failure<UserError.UsernameAlreadyExists>>(registration2)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `registration, login and find by token test`(trxManager: TransactionManager){
        val userServices = createUserService(trxManager, TestClock())
        val registration = userServices.registration(UUID.fromString(
            "00000000-0000-0000-0000-000000000000"),
            Email("Antonio@email.com"),
            "Antonio",
            "Password3")
        assertIs<Success<User>>(registration)
        val login = userServices.login("Antonio", "Password3")
        assertIs<Success<TokenExternalInfo>>(login)
        val user = userServices.getUserByToken(login.value.tokenValue)
        assertIs<Success<User>>(user)
        assertEquals(user.value.name, "Antonio")
        assertEquals(user.value.email, Email("Antonio@email.com"))
    }







}