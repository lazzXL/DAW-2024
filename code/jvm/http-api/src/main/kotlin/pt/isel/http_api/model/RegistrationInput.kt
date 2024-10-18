package pt.isel.http_api.model

import pt.isel.Email
import java.util.*

/**
 * Input model for registration.
 * @property invitation the invitation code.
 * @property name the name of the user.
 * @property email the email of the user.
 * @property password the password of the user.
 */
data class RegistrationInput(
    val invitation : UUID,
    val name: String,
    val email: Email,
    val password: String
)