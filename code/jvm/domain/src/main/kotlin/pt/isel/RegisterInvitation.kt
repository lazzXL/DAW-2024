package pt.isel

import java.util.*

/**
 * Represents a register invitation.
 * @property id the id of the invitation.
 * @property code the code of the invitation.
 */
data class RegisterInvitation(
    val id: UInt,
    val code: UUID
)


