import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class MessageServicesTest {

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
                repoChannel.clear()
                repoChannelInvitation.clear()
                repoRegisterInvitation.clear()
                repoUser.clear()
                repoUser.createUser("Paulo",
                    Email("email@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password1")))
                repoUser.createUser("Miguel",
                    Email("miguel@email.com"),
                    PasswordValidationInfo(BCryptPasswordEncoder().encode("Password2")))

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
    fun `send a message and get it` (trxManager: TransactionManager){
        val msgService = MessageServices(trxManager)
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val sentMessage = msgService.sendMessage("message", channel.value.id, user.value.id)
        assertIs<Success<Message>>(sentMessage)
        val foundMsg = msgService.getMessages(channel.value.id, user.value.id)
        assertIs<Success<List<Message>>>(foundMsg)
        assertEquals(foundMsg.value[0], sentMessage.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to send a message with a user that is not a participant expecting error` (trxManager: TransactionManager){
        val msgService = MessageServices(trxManager)
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        val user2 = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val sentMessage = msgService.sendMessage("message", channel.value.id, user.value.id)
        assertIs<Success<Message>>(sentMessage)
        val foundMsg = msgService.getMessages(channel.value.id, user2.value.id)
        assertIs<Failure<MessageError.ParticipantNotFound>>(foundMsg)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to send a message with READ-ONLY permission` (trxManager: TransactionManager){
        val msgService = MessageServices(trxManager)
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        val user2 = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channel = ChannelServices(trxManager).createChannel("TestChannel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        ParticipantServices(trxManager).joinPublicChannel(user2.value, channel.value.id)
        val sentMessage = msgService.sendMessage("message", channel.value.id, user2.value.id)
        assertIs<Failure<MessageError.NoWritePermission>>(sentMessage)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to send a message to a channel that does not exist` (trxManager: TransactionManager){
        val msgService = MessageServices(trxManager)
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val sentMessage = msgService.sendMessage("message", 10U, user.value.id)
        assertIs<Failure<MessageError.ChannelNotFound>>(sentMessage)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try do get a message not being on the channel` (trxManager: TransactionManager){
        val msgService = MessageServices(trxManager)
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        val user2 = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val sendMessage = msgService.sendMessage("message", channel.value.id, user2.value.id)
        assertIs<Failure<MessageError.ParticipantNotFound>>(sendMessage)
    }
}




