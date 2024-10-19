import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ChannelInvitationServicesTests {

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
                repoUser.createUser("Alice",
                    Email("alice@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password1")))
                repoUser.createUser("Charles",
                    Email("charles@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password1")))
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
    fun `create a channel invitation`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val invitation = channelInvitationServices.createInvitation(channel.value.id,Permission.READ_WRITE,user.value.id)
        assertIs<Success<ChannelInvitation>>(invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try create a channel invitation for a channel that does not exist`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val invitation = channelInvitationServices.createInvitation(111u,Permission.READ_WRITE,user.value.id)
        assertIs<Failure<ChannelInvitationError.ChannelNotFound>>(invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try create a channel invitation without being the channel admin`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val user2 = createUserService(trxManager, TestClock()).findByName("Charles")
        assertIs<Success<User>>(user2)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val invitation = channelInvitationServices.createInvitation(channel.value.id,Permission.READ_WRITE,user2.value.id)
        assertIs<Failure<ChannelInvitationError.UserIsNotAdmin>>(invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a channel invitation`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val invitation = channelInvitationServices.createInvitation(channel.value.id,Permission.READ_WRITE,user.value.id)
        assertIs<Success<ChannelInvitation>>(invitation)
        val delete = channelInvitationServices.deleteInvitation(invitation.value.id,user.value.id)
        assertIs<Success<Unit>>(invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to delete a channel invitation that does not exist`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val delete = channelInvitationServices.deleteInvitation(22u,user.value.id)
        assertIs<Failure<ChannelInvitationError.InvitationNotFound>>(delete)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to delete a channel invitation without being an admin`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Alice")
        assertIs<Success<User>>(user)
        val user2 = createUserService(trxManager, TestClock()).findByName("Charles")
        assertIs<Success<User>>(user2)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val channelInvitationServices = ChannelInvitationServices(trxManager)
        val invitation = channelInvitationServices.createInvitation(channel.value.id,Permission.READ_WRITE,user.value.id)
        assertIs<Success<ChannelInvitation>>(invitation)
        val delete = channelInvitationServices.deleteInvitation(invitation.value.id,user2.value.id)
        assertIs<Failure<ChannelInvitationError.UserIsNotAdmin>>(delete)
    }

}