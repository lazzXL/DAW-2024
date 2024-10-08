package pt.isel

import java.util.*

data class Invitation(
    val id: UInt,
    val code: UUID,
    val channel: Channel,
    val permission: Permission,
)


