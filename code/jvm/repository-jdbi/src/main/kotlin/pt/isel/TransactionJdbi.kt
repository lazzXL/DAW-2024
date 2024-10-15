package pt.isel

import org.jdbi.v3.core.Handle

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoChannel = RepositoryChannelJdbi(handle)
    override val repoUser = RepositoryUserJdbi(handle)
    override val repoParticipant = RepositoryParticipantJdbi(handle)
    override val repoMessage = RepositoryMessageJdbi(handle)
    override val repoChannelInvitation = RepositoryChannelInvitationJdbi(handle)
    override val repoRegisterInvitation = RepositoryRegisterInvitationJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }
}
