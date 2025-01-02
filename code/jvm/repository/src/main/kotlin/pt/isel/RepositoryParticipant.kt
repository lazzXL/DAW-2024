package pt.isel

interface RepositoryParticipant: Repository<Participant> {

    fun isParticipant(channelId : UInt, userId : UInt) : Participant?

    fun createParticipant(user: User, channel: Channel, permission: Permission): Participant

    fun getParticipantsFromChannel(channelId: UInt): List<Participant>

    fun setInactiveParticipant(id: UInt)
}
