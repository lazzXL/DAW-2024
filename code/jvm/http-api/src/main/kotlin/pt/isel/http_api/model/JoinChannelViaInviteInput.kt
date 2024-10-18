package pt.isel.http_api.model

import java.util.*

/**
 * Input model for joining a channel via an invite.
 * @property code the code of the invite.
 */
data class JoinChannelViaInviteInput(
    val code: UUID,
)