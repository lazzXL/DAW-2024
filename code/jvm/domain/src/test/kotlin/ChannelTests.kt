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
        val id = 321U
        val adminId = 123U
        val name = "Test Channel"
        val description = "This is a test channel"
        val visibility = Visibility.PUBLIC

        val channel = Channel(id, name, adminId, description, visibility)

        assertEquals(id, channel.id)
        assertEquals(name, channel.name)
        assertEquals(adminId, channel.adminID)
        assertEquals(description, channel.description)
        assertEquals(visibility, channel.visibility)
    }

    @Test
    fun `Channel initialization with invalid name length`() {
        val id = 321U
        val admin = 123U
        val name = "T"
        val description = "This is a test channel"
        val visibility = Visibility.PUBLIC

        assertFailsWith<IllegalArgumentException> {
            Channel(id, name, admin, description, visibility)
        }

    }

    @Test
    fun `Channel initialization with invalid description length`() {
        val id = 9876U
        val admin = 9883U
        val name = "Test Channel"
        val description = "This is a test channel".repeat(100)
        val visibility = Visibility.PUBLIC

        assertFailsWith<IllegalArgumentException> {
            Channel(id, name, admin, description, visibility)
        }
    }
}
