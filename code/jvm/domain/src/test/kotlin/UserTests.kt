import pt.isel.User
import pt.isel.Email
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserTest {

    @Test
    fun `User initialization with valid parameters`() {
        val id = UUID.randomUUID()
        val name = "Test User"
        val email = Email("test@example.com")
        val password = "password"
        val channels = setOf(UUID.randomUUID())

        val user = User(id, name, email, password, channels)

        assertEquals(id, user.id)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(password, user.password)
        assertEquals(channels, user.channels)
    }

    @Test
    fun `User initialization with invalid name length`() {
        val id = UUID.randomUUID()
        val name = "T"
        val email = Email("test@example.com")
        val password = "password"
        val channels = setOf(UUID.randomUUID())

        assertFailsWith<IllegalArgumentException> {
            User(id, name, email, password, channels)
        }
    }
}