package pt.isel.http_api.model

import java.util.*

data class LoginInput(
    val email: String,
    val password: String
)