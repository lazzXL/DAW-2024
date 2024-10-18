package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*
import pt.isel.http_api.model.CreateInvitationInput
import pt.isel.http_api.model.handleRegisterInvitationFailure
import java.util.*

/**
 * REST Controller for managing register invitation.
 * @property invitationService the services for the register invitation.
 */
@RestController
@RequestMapping("/registerInvitation")
class RegisterInvitationController (
    private val invitationService: RegisterInvitationServices
) {
    /**
     * Creates a register invitation.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @PostMapping("/create")
    fun createInvitation(authenticatedUser: AuthenticatedUser): ResponseEntity<RegisterInvitation> {
        return when (val result: Either<RegisterInvitationError, RegisterInvitation> = invitationService.createInvitation()) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleRegisterInvitationFailure(result.value)
        }
    }
    /**
     * Deletes a register invitation.
     * @param invitationId the invitation id.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @DeleteMapping("/delete/{invitationId}")
    fun deleteInvitation(@PathVariable invitationId: UUID, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<RegisterInvitationError, Unit> = invitationService.deleteInvitation(invitationId)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleRegisterInvitationFailure(result.value)
        }
    }
}