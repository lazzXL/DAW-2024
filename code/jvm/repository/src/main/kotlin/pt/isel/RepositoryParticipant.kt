package pt.isel

interface RepositoryParticipant: Repository<Participant> {
    fun joinChannel(channel: Channel, user: User, permission: Permission): Channel
    fun leaveChannel(channel: Channel, user: User): Channel

    fun isParticipant(channelId : UInt, userId : UInt) : Boolean


}
