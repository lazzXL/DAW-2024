package pt.isel

import java.util.UUID

interface RepositoryInvite:Repository<Invitation> {
    fun findByCode(code:UUID):Invitation?
}
