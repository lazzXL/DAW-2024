package pt.isel


interface RepositoryChannel : Repository<Channel> {
    fun createChannel(name : String, description : String, adminID : UInt, visibility: Visibility) : Channel

    fun findByName(name : String) : Channel?
    fun findAllByUser(userID: UInt, name:String? = null): List<Channel>
    fun getPublicChannels(name:String? = null): List<Channel>



}