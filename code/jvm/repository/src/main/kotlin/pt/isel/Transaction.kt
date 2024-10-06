package pt.isel

interface Transaction {
    val repoChannel: RepositoryChannel
    val repoUser : RepositoryUser
    val repoParticipant: RepositoryParticipant
    val repoInvite : RepositoryInvite


    fun rollback()
}