package pt.isel.http_api.model

import java.util.*

data class RegistrationInput(
    val invitation : UUID,
    val name: String,
    val email: String,
    val password: String
)