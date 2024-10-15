package pt.isel.http_api.model

data class CreateChannelInput(
    val name: String,
    val description: String,
    val adminId: Int,
    val isPublic: Boolean
)