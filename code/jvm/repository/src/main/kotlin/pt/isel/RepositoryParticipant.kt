package pt.isel

interface RepositoryParticipant: Repository<Participant> {

    fun isParticipant(channelId : UInt, userId : UInt) : Boolean

    fun createParticipant(user: User, channel: Channel, permission: Permission): Participant
}
