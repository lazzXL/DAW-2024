package pt.isel.http_api.model

/**
    * Input model for creating a channel.
    * @property name the name of the channel.
    * @property description the description of the channel.
    * @property isPublic boolean value representing the visibility of the channel.
 */
data class CreateChannelInput(
    val name: String,
    val description: String,
    val isPublic: Boolean
)