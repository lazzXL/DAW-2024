import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import pt.isel.temp.BCryptPasswordEncoder
import pt.isel.temp.Sha256TokenEncoder
import pt.isel.temp.UsersDomain
import pt.isel.temp.UsersDomainConfig
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ParticipantServicesTest {
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
                repoUser.createUser(
                    "Paulo",
                    Email("email@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password1"))
                )
                repoUser.createUser(
                    "Miguel",
                    Email("miguel@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password2"))
                )
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
    fun `join a channel by invite` (trxManager: TransactionManager){
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val invitation = ChannelInvitationServices(trxManager).createInvitation(channel.value.id, Permission.READ_WRITE, user.value.id)
        assertIs<Success<ChannelInvitation>>(invitation)
        val participant = ParticipantServices(trxManager).joinChannelByInvite(user2.value, invitation.value.code)
        assertIs<Success<Participant>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to join a channel with an invalid code` (trxManager: TransactionManager){
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user2)
        val participant = ParticipantServices(trxManager).joinChannelByInvite(user2.value, UUID.randomUUID())
        assertIs<Failure<ParticipantError.InvalidInvite>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to join a channel being already there` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val invitation = ChannelInvitationServices(trxManager).createInvitation(channel.value.id, Permission.READ_WRITE, user.value.id)
        assertIs<Success<ChannelInvitation>>(invitation)
        val participant = ParticipantServices(trxManager).joinChannelByInvite(user.value, invitation.value.code)
        assertIs<Failure<ParticipantError.UserAlreadyInChannel>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `join a public channel` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val participant = ParticipantServices(trxManager).joinPublicChannel(user2.value, channel.value.id )
        assertIs<Success<Participant>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to join a private channel without invitation` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PRIVATE)
        assertIs<Success<Channel>>(channel)
        val participant = ParticipantServices(trxManager).joinPublicChannel(user2.value, channel.value.id )
        assertIs<Failure<ParticipantError.ChannelNotPublic>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to join a public channel being already there` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PRIVATE)
        assertIs<Success<Channel>>(channel)
        val participant = ParticipantServices(trxManager).joinPublicChannel(user.value, channel.value.id )
        assertIs<Failure<ParticipantError.UserAlreadyInChannel>>(participant)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `leave a channel` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val participant = ParticipantServices(trxManager).joinPublicChannel(user2.value, channel.value.id )
        assertIs<Success<Participant>>(participant)
        val leave = ParticipantServices(trxManager).leaveChannel(channel.value.id, user2.value.id)
        assertIs<Success<Unit>>(leave)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to leave a channel not being there` (trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Miguel")
        val user2 = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val leave = ParticipantServices(trxManager).leaveChannel(channel.value.id, user2.value.id)
        assertIs<Failure<ParticipantError.ParticipantNotFound>>(leave)
    }
}