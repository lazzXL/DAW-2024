package pt.isel.mem

import pt.isel.Channel
import pt.isel.ChannelInvitation
import pt.isel.Permission
import pt.isel.RepositoryChannelInvitation
import java.util.*

private val channelInvitations = mutableListOf<ChannelInvitation>()

class RepositoryChannelInvitationInMem : RepositoryChannelInvitation {

    override fun findById(id: UInt): ChannelInvitation? =
        channelInvitations.firstOrNull{it.id == id}

    override fun findAll(): List<ChannelInvitation> =
        channelInvitations.toList()

    override fun save(entity: ChannelInvitation) {
        channelInvitations.removeIf { entity.id == it.id }
        channelInvitations.add(entity)
    }

    override fun deleteById(id: UInt) {
        channelInvitations.removeIf { it.id == id }
    }

    override fun clear() =
        channelInvitations.clear()

    override fun findByCode(code: UUID): ChannelInvitation? =
        channelInvitations.firstOrNull{it.code == code}

    override fun createInvitation(code: UUID, permission: Permission, channel: Channel): ChannelInvitation {
        val invitation = ChannelInvitation(channelInvitations.count().toUInt(), code, channel, permission)
        channelInvitations.add(invitation)
        return invitation
    }
}