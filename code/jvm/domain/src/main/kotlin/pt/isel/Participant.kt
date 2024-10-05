package pt.isel

import java.util.*

/**
 * Represents a participant in a channel
 * @property id the id of the participant
 * @property permission the permission of the participant
 */
data class Participant(
    val id : UUID,
    val permission : Permission
)

