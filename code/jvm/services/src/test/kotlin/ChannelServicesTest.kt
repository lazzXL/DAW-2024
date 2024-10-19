import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.mem.TransactionManagerInMem
import java.util.*
import java.util.stream.Stream
import kotlin.collections.List
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes


class ChannelServicesTest {

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
    fun `create a channel and get it by id`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        val foundChannel = channelServices.getChannel(channel.value.id, user.value.id)
        assertIs<Success<Channel>>(foundChannel)
        assertNotNull(foundChannel.value)
        assertEquals(foundChannel.value.name, "Channel")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a channel with a name that already exists expecting error`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        val channelWithError = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        assertIs<Failure<ChannelError.ChannelNameAlreadyExists>>(channelWithError)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `try to get a private channel not being participant`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        val user2 = createUserService(trxManager, TestClock()).findByName("Miguel")
        assertIs<Success<User>>(user)
        assertIs<Success<User>>(user2)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PRIVATE)
        assertIs<Success<Channel>>(channel)
        val foundChannel = channelServices.getChannel(channel.value.id, user2.value.id)
        assertIs<Failure<ChannelError.ChannelNotPublic>>(foundChannel)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get joined channels of a user`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        val channel2 = channelServices.createChannel("Channel2", "Description", user.value, Visibility.PRIVATE)
        assertIs<Success<Channel>>(channel)
        assertIs<Success<Channel>>(channel2)
        val joinedChannels = channelServices.getJoinedChannels(user.value.id)
        assertIs<Success<List<Channel>>>(joinedChannels)
        assertEquals(joinedChannels.value.size, 2)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get all public channels`(trxManager: TransactionManager){
        val user = createUserService(trxManager, TestClock()).findByName("Paulo")
        assertIs<Success<User>>(user)
        val channelServices = ChannelServices(trxManager)
        val channel = channelServices.createChannel("Channel", "Description", user.value, Visibility.PUBLIC)
        val channel2 = channelServices.createChannel("Channel2", "Description", user.value, Visibility.PRIVATE)
        val channel3 = channelServices.createChannel("Channel3", "Description", user.value, Visibility.PUBLIC)
        assertIs<Success<Channel>>(channel)
        assertIs<Success<Channel>>(channel2)
        assertIs<Success<Channel>>(channel3)
        val joinedChannels = channelServices.getPublicChannels()
        assertIs<Success<List<Channel>>>(joinedChannels)
        assertEquals(joinedChannels.value.size, 2)
    }

}

