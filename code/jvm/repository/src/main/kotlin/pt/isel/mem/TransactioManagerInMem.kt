package pt.isel.mem


import pt.isel.Transaction
import pt.isel.TransactionManager

class TransactionManagerInMem : TransactionManager {
    private val repoChannels = RepositoryChannelInMem()
    private val repoUsers = RepositoryUserInMem()
    private val repoParticipants = RepositoryParticipantInMem()
    private val repoMessages = RepositoryMessageInMem()
    private val repoRegisterInvitation = RepositoryRegisterInvitationInMem()
    private val repositoryChannelInvitation = RepositoryChannelInvitationInMem()


    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(repoChannels, repoUsers, repoParticipants, repositoryChannelInvitation, repoMessages, repoRegisterInvitation))
}