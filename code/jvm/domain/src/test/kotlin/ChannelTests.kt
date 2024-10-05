import org.junit.jupiter.api.assertThrows
import pt.isel.Channel
import pt.isel.Visibility
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChannelTest {

    @Test
    fun `Channel initialization with valid parameters`() {
        val id = UUID.randomUUID()
        val admin = UUID.randomUUID()
        val name = "Test Channel"
        val description = "This is a test channel"
        val visibility = Visibility.PUBLIC // Assuming Visibility is an enum with PUBLIC as one of its values

        val channel = Channel(id, name, admin, description, visibility)

        assertEquals(id, channel.id)
        assertEquals(name, channel.name)
        assertEquals(admin, channel.admin)
        assertEquals(description, channel.description)
        assertEquals(visibility, channel.visibility)
    }

    @Test
    fun `Channel initialization with invalid name length`() {
        val id = UUID.randomUUID()
        val admin = UUID.randomUUID()
        val name = "T"
        val description = "This is a test channel"
        val visibility = Visibility.PUBLIC

        assertFailsWith<IllegalArgumentException> {
            Channel(id, name, admin, description, visibility)
        }

    }

    @Test
    fun `Channel initialization with invalid description length`() {
        val id = UUID.randomUUID()
        val admin = UUID.randomUUID()
        val name = "Test Channel"
        val description = "This is a test channel".repeat(100)
        val visibility = Visibility.PUBLIC

        assertFailsWith<IllegalArgumentException> {
            Channel(id, name, admin, description, visibility)
        }
    }
}