import pt.isel.User
import pt.isel.Email
import pt.isel.PasswordValidationInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserTest {

    @Test
    fun `User initialization with valid parameters`() {
        val id = 321U
        val name = "TestUser"
        val email = Email("test@example.com")
        val password = PasswordValidationInfo("ValidPassword1!")
        val user = User(id, name, email, password)

        assertEquals(id, user.id)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(password, user.password)
    }

    @Test
    fun `User initialization with invalid name length`() {
        val id = 321U
        val name = "T"
        val email = Email("test@example.com")
        val password = PasswordValidationInfo("ValidPassword1!")
        assertFailsWith<IllegalArgumentException> {
            User(id, name, email, password)
        }
    }

    @Test
    fun `User initialization with blank name`() {
        val id = 321U
        val name = "    "
        val email = Email("test@example.com")
        val password = PasswordValidationInfo("ValidPassword1!")
        assertFailsWith<IllegalArgumentException> {
            User(id, name, email, password)
        }
    }

    @Test
    fun `Email initialization with invalid email`() {
        assertFailsWith<IllegalArgumentException> {
            Email("invalidemail")
        }
    }
}