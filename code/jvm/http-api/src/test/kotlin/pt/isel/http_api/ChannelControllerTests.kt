import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.*
import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.RegistrationInput
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ChannelControllerTests {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )
    }


    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create a channel`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create a channel with invalid name`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "c"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)
        assertFailsWith<IllegalArgumentException> {
            controllerChannel.createChannel(createInput, authUser)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test if get channel retrieves a channel`(trxManager: TransactionManager) {

        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)
        val channel = response.body!!

        val responseGet = controllerChannel.getChannel(channel.id, authUser)
        assertEquals(HttpStatus.OK, responseGet.statusCode)
        assertEquals(channel, responseGet.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test if get channel retrieves a channel with invalid id`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)
        val channel = response.body!!

        val responseGet = controllerChannel.getChannel(channel.id + 1u, authUser)
        assertEquals(HttpStatus.NOT_FOUND, responseGet.statusCode)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test if get public channels retrieves all public channels`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)

        val response = controllerChannel.createChannel(createInput, authUser)
        val channel = response.body!!

        val responseGet = controllerChannel.getPublicChannels(null, null, null, authUser)
        assertEquals(HttpStatus.OK, responseGet.statusCode)
        assertEquals(listOf(channel), responseGet.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get joined channels with valid user`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, true)

        controllerChannel.createChannel(createInput, authUser)

        val response = controllerChannel.getJoinedChannels(null, null, null, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body!!.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get joined channels with no joined channels`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)

        val response = controllerChannel.getJoinedChannels(null, null, null, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get public channels with no public channels`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)

        val response = controllerChannel.getPublicChannels(null, null, null, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test create a private channel`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName = "privateChannel"
        val channelDescription = "privateDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput = CreateChannelInput(channelName, channelDescription, false)

        val response = controllerChannel.createChannel(createInput, authUser)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(Visibility.PRIVATE, response.body!!.visibility)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get public channels with name, limit, and skip`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName1 = "channelName1"
        val channelDescription1 = "channelDescription1"
        val channelName2 = "channelName2"
        val channelDescription2 = "channelDescription2"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput1 = CreateChannelInput(channelName1, channelDescription1, true)
        val createInput2 = CreateChannelInput(channelName2, channelDescription2, true)

        controllerChannel.createChannel(createInput1, authUser)
        controllerChannel.createChannel(createInput2, authUser)

        val response = controllerChannel.getPublicChannels(channelName1, 1, 0, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body!!.size)
        assertEquals(channelName1, response.body!![0].name)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get joined channels with name, limit, and skip`(trxManager: TransactionManager) {
        val channelService = createChannelService(trxManager)
        val channelName1 = "channelName1"
        val channelDescription1 = "channelDescription1"
        val channelName2 = "channelName2"
        val channelDescription2 = "channelDescription2"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createInput1 = CreateChannelInput(channelName1, channelDescription1, true)
        val createInput2 = CreateChannelInput(channelName2, channelDescription2, true)

        controllerChannel.createChannel(createInput1, authUser)
        controllerChannel.createChannel(createInput2, authUser)

        val response = controllerChannel.getJoinedChannels(channelName1, 1, 0, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body!!.size)
        assertEquals(channelName1, response.body!![0].name)
    }
}




