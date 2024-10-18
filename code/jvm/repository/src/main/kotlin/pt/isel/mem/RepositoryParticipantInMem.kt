package pt.isel.mem

import pt.isel.*

class RepositoryParticipantInMem : RepositoryParticipant {
    private val participants = mutableListOf<Participant>()

    override fun findById(id: UInt): Participant? =
        participants.firstOrNull{it.id == id}

    override fun findAll(): List<Participant> =
        participants.toList()

    override fun save(entity: Participant) {
        participants.removeIf { it.id == entity.id}
        participants.add(entity)
    }

    override fun deleteById(id: UInt) {
        participants.removeIf { it.id == id}
    }

    override fun clear() =
        participants.clear()

    override fun isParticipant(channelId: UInt, userId: UInt): Participant? =
        TODO()

    override fun createParticipant(user: User, channel: Channel, permission: Permission): Participant =
        Participant(participants.count().toUInt(), user, channel, permission)
}