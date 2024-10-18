package pt.isel.http_api.model


/**
 * Input model for sending a message.
 * @property content the content of the message.
 * @property channelId the id of the channel.
 */
data class SendMessageInput(
    val content: String,
    val channelId: UInt
)