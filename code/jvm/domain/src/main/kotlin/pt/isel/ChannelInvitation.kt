package pt.isel

import java.util.*

/**
 * Represents a channel invitation.
 */
data class ChannelInvitation(
    val id: UInt,
    val code: UUID,
    val channel: Channel,
    val permission: Permission,
)


