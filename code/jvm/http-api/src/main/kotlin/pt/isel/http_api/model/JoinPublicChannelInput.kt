package pt.isel.http_api.model

/**
 * Input model for joining a public channel.
 * @property channelId the id of the channel.
 */
data class JoinPublicChannelInput(
       val channelId: UInt
)