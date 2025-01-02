import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.*
import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.SendMessageInput
import java.util.stream.Stream
import kotlin.test.assertIs

class MessageControllerTests {
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
    fun `test send a message`(trxManager: TransactionManager) {
        val controllerMessage = MessageController(createMessageService(trxManager))


        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val channelResponse = createChannel(trxManager, authUser)

        val messageInput = SendMessageInput("messageContent", channelResponse.id)

        controllerMessage.sendMessage(messageInput, authUser).let { resp ->
            assertEquals(HttpStatus.CREATED, resp.statusCode)
            assertNotNull(resp.body)
            assertIs<Message>(resp.body)
            val message = resp.body as Message
            assertEquals(messageInput.content, message.content)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get messages with valid parameters`(trxManager: TransactionManager) {
        val messageService = createMessageService(trxManager)
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createChannelInput = CreateChannelInput(channelName, channelDescription, true)
        val channelResponse = controllerChannel.createChannel(createChannelInput, authUser)
        val channelId = channelResponse.body!!.id

        val controllerMessage = MessageController(messageService)
        val sendMessageInput = SendMessageInput("This is a test message", channelId)
        controllerMessage.sendMessage(sendMessageInput, authUser)

        val response = controllerMessage.getMessages(channelId, null, null, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body!!.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get messages with no messages`(trxManager: TransactionManager) {
        val messageService = createMessageService(trxManager)
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createChannelInput = CreateChannelInput(channelName, channelDescription, true)
        val channelResponse = controllerChannel.createChannel(createChannelInput, authUser)
        val channelId = channelResponse.body!!.id

        val controllerMessage = MessageController(messageService)

        val response = controllerMessage.getMessages(channelId, null, null, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(0, response.body!!.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `test get messages with limit and skip`(trxManager: TransactionManager) {
        val messageService = createMessageService(trxManager)
        val channelService = createChannelService(trxManager)
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val authUser = AuthenticatedUser(createUser(trxManager), getToken(trxManager))

        val controllerChannel = ChannelController(channelService)
        val createChannelInput = CreateChannelInput(channelName, channelDescription, true)
        val channelResponse = controllerChannel.createChannel(createChannelInput, authUser)
        val channelId = channelResponse.body!!.id

        val controllerMessage = MessageController(messageService)
        val messages = listOf(
            SendMessageInput("Message 1", channelId),
            SendMessageInput("Message 2", channelId),
            SendMessageInput("Message 3", channelId)
        )

        messages.forEach { controllerMessage.sendMessage(it, authUser) }

        val response = controllerMessage.getMessages(channelId, 2, 1, authUser)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body!!.size)
        assertEquals("Message 2", response.body!![0].content)
        //assertEquals("Message 3", response.body!![1].content)
    }

}





