import pt.isel.Message
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MessageTest {

    @Test
    fun `Message initialization with valid parameters`() {
        val content = "This is a test message"
        val date = LocalDateTime.now()
        val user = UUID.randomUUID()
        val channelID = UUID.randomUUID()

        val message = Message(content, date, user, channelID)

        assertEquals(content, message.content)
        assertEquals(date, message.date)
        assertEquals(user, message.user)
        assertEquals(channelID, message.channelID)
    }

    @Test
    fun `Message initialization with invalid content length`() {
        val content = "This is a test message".repeat(1000) // Assuming this is more than MAX_MESSAGE_LENGTH
        val date = LocalDateTime.now()
        val user = UUID.randomUUID()
        val channelID = UUID.randomUUID()

        assertFailsWith<IllegalArgumentException> {
            Message(content, date, user, channelID)
        }
    }
}