package pt.isel.http_api.model

import pt.isel.Email
import java.util.*

data class RegistrationInput(
    val invitation : UUID,
    val name: String,
    val email: Email,
    val password: String
)