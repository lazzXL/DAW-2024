package pt.isel

import java.util.UUID

interface RepositoryInvite:Repository<Invitation> {
    fun findByCode(code:UUID):Invitation?
    fun createInvitation(code: UUID, permission: Permission, channel: Channel): Invitation
}
