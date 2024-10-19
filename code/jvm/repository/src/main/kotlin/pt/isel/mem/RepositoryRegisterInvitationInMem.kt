package pt.isel.mem

import pt.isel.RegisterInvitation
import pt.isel.RepositoryRegisterInvitation
import java.util.*

val registerInvitations = mutableListOf<RegisterInvitation>()

class RepositoryRegisterInvitationInMem : RepositoryRegisterInvitation {

    override fun findById(id: UInt): RegisterInvitation? =
        registerInvitations.firstOrNull{it.id == id}

    override fun findAll(): List<RegisterInvitation> =
        registerInvitations.toList()

    override fun save(entity: RegisterInvitation) {
        registerInvitations.removeIf { it.id == entity.id }
        registerInvitations.add(entity)
    }

    override fun deleteById(id: UInt) {
        registerInvitations.removeIf { it.id == id }
    }

    override fun clear() =
        registerInvitations.clear()

    override fun findByCode(code: UUID): RegisterInvitation? =
        registerInvitations.firstOrNull{it.code == code}

    override fun deleteByCode(code: UUID) {
        registerInvitations.removeIf { it.code == code }
    }

    override fun createInvitation(code: UUID): RegisterInvitation {
        val invitation = RegisterInvitation(registerInvitations.count().toUInt(), code)
        registerInvitations.add(invitation)
        return invitation
    }
}