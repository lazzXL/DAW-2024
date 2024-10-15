package pt.isel.http_api.model

import java.util.*

data class LoginInput(
    val name: String,
    val password: String
)