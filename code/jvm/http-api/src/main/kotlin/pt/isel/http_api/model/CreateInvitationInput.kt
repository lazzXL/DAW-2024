package pt.isel.http_api.model

import pt.isel.Permission

data class CreateInvitationInput (
    val channelID: UInt,
    val permission: Permission,
)