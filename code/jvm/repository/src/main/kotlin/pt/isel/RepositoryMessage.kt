package pt.isel

interface RepositoryMessage : Repository<Message> {
    fun sendMessage()
}