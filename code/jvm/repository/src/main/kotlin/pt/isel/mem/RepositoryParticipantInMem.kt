package pt.isel.mem

import pt.isel.*

val participants = mutableListOf<Participant>()

class RepositoryParticipantInMem : RepositoryParticipant {


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

    override fun isParticipant(channelId: UInt, userId: UInt): Participant? {
        val participants = participants.filter { it.channel.id == channelId }
        return participants.find { it.user.id == userId  }
    }

    override fun createParticipant(user: User, channel: Channel, permission: Permission): Participant {
        val participant = Participant(participants.count().toUInt(), user, channel, permission)
        participants.add(participant)
        return participant
    }

    override fun getParticipantsFromChannel(channelId: UInt): List<Participant> {
        TODO("Not yet implemented")
    }
}