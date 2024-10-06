package pt.isel

import java.util.UUID

interface RepositoryInvite:Repository<Invite> {
    fun findByCode(code:UUID):Invite?
}
