package pt.isel

import java.util.UUID

interface RepositoryChannelInvitation:Repository<ChannelInvitation> {
    fun findByCode(code:UUID):ChannelInvitation?
    fun createInvitation(code: UUID, permission: Permission, channel: Channel): ChannelInvitation
}
