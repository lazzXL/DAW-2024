package pt.isel

interface Transaction {
    val repoChannel: RepositoryChannel
    val repoUser : RepositoryUser
    val repoParticipant: RepositoryParticipant
    val repoChannelInvitation : RepositoryChannelInvitation
    val repoMessage : RepositoryMessage
    val repoRegisterInvitation : RepositoryRegisterInvitation


    fun rollback()
}