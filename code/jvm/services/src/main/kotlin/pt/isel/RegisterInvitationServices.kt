package pt.isel

import jakarta.inject.Named
import java.util.*

sealed class RegisterInvitationError {
    data object InvitationNotFound : RegisterInvitationError()
}

@Named
class RegisterInvitationServices(
    private val trxManager: TransactionManager
) {


    fun createInvitation(): Either<RegisterInvitationError, RegisterInvitation> = trxManager.run {
        val invitation: RegisterInvitation = repoRegisterInvitation.createInvitation(UUID.randomUUID())
        success(invitation)
    }

    fun deleteInvitation(invitationId: UUID): Either<RegisterInvitationError, Unit> = trxManager.run {
        repoRegisterInvitation.findByCode(invitationId) ?: return@run failure(RegisterInvitationError.InvitationNotFound)
        repoRegisterInvitation.deleteByCode(invitationId)
        success(Unit)
    }


}



