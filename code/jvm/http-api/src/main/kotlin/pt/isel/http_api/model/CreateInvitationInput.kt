package pt.isel.http_api.model

data class CreateInvitationInput (
    val channelID: UInt,
    val permission: String,
    val userId: UInt,
)