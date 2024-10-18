package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*
import pt.isel.http_api.model.CreateInvitationInput
import pt.isel.http_api.model.handleChannelFailure
import pt.isel.http_api.model.handleChannelInvitationFailure
import pt.isel.http_api.model.handleParticipantFailure


@RestController
@RequestMapping("/invitation")
class ChannelInvitationController(
    private val invitationService: ChannelInvitationServices
) {
    //TODO: CAN ALL USERS INVITE WITH READ_WRITE? PROBLEM; DELETE INVITATION MISSING
    @PostMapping("/create") // Verified
    fun createInvitation(@RequestBody invitationInput: CreateInvitationInput, authenticatedUser: AuthenticatedUser): ResponseEntity<ChannelInvitation> {
        return when (val result: Either<ChannelInvitationError, ChannelInvitation> = invitationService.createInvitation(invitationInput.channelID, invitationInput.permission, authenticatedUser.user.id)) {
             is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
             is Failure -> handleChannelInvitationFailure(result.value)
         }
    }

    @DeleteMapping("/delete/{invitationId}")
    fun leaveChannel(@PathVariable invitationId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ChannelInvitationError, Unit> = invitationService.deleteInvitation(invitationId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelInvitationFailure(result.value)
        }
    }
}

