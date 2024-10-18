package pt.isel.mem

import pt.isel.*

class TransactionInMem(
    override val repoChannel : RepositoryChannel,
    override val repoUser : RepositoryUser,
    override val repoParticipant : RepositoryParticipant,
    override val repoChannelInvitation : RepositoryChannelInvitation,
    override val repoMessage: RepositoryMessage,
    override val repoRegisterInvitation: RepositoryRegisterInvitation
) : Transaction {
    override fun rollback(): Unit = throw UnsupportedOperationException()
}