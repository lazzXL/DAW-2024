package pt.isel.http_api.model

import pt.isel.Permission

/**
 * Input model for creating an invitation.
 * @property channelID the id of the channel.
 * @property permission the permission of the invitation.
 */
data class CreateInvitationInput (
    val channelID: UInt,
    val permission: Permission,
)