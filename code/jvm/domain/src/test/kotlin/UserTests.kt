import pt.isel.User
import pt.isel.Email
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserTest {

    @Test
    fun `User initialization with valid parameters`() {
        val id = 123U
        val token = UUID.randomUUID()
        val name = "Test User"
        val email = Email("test@example.com")
        val password = "password"

        val user = User(id, token, name, email, password)

        assertEquals(id, user.id)
        assertEquals(token, user.token)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(password, user.password)
    }

    @Test
    fun `User initialization with invalid name length`() {
        val id = 321U
        val token = UUID.randomUUID()
        val name = "T"
        val email = Email("test@example.com")
        val password = "password"
        assertFailsWith<IllegalArgumentException> {
            User(id, token, name, email, password)
        }
    }
}



