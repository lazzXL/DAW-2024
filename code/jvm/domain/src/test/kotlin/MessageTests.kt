import pt.isel.*
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MessageTest {
    private val channel = Channel(123U, "Channel", 321U, "description", Visibility.PUBLIC)

    private val user = User(321U, UUID.randomUUID(), "Paulo", Email("paulo@email.com"), "123456")

    private val participant = Participant(456U, user, channel, Permission.READ_WRITE)


    @Test
    fun `Message initialization with valid parameters`() {
        val id = 123U
        val content = "This is a test message"
        val date = LocalDateTime.now()
        val sender = participant
        val message = Message(id, content, date, sender)

        assertEquals(id, message.id)
        assertEquals(content, message.content)
        assertEquals(date, message.date)
        assertEquals(sender, message.sender)
    }

    @Test
    fun `Message initialization with invalid content length`() {
        val id = 321U
        val content = "This is a test message".repeat(1000)
        val date = LocalDateTime.now()
        val sender = participant

        assertFailsWith<IllegalArgumentException> {
            Message(id, content, date, sender)
        }
    }

}



