package pt.isel

import java.util.*

interface RepositoryRegisterInvitation:Repository<RegisterInvitation> {
    fun findByCode(code: UUID):RegisterInvitation?
    fun createInvitation(code: UUID): RegisterInvitation
}