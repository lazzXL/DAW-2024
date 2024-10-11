package pt.isel

import org.jdbi.v3.core.Handle

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoChannel = RepositoryChannelJdbi(handle)
    override val repoUser = RepositoryUserJdbi(handle)
    override val repoParticipant = TODO("Not yet implemented")
    override val repoMessage = TODO("Not yet implemented")
    override val repoInvite =  TODO("Not yet implemented")

    override fun rollback() {
        handle.rollback()
    }
}
