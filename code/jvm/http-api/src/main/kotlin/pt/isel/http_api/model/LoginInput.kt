package pt.isel.http_api.model

import java.util.*
/**
 * Input model for logging in.
 * @property name the name of the user.
 * @property password the password of the user.
 */
data class LoginInput(
    val name: String,
    val password: String
)