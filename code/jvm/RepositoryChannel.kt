import java.util.UUID

interface RepositoryChannel {
    fun createChannel(name : String, description : String, adminID : UUID, visibility: Visibility)

}