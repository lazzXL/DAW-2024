package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.CreateInvitationInput
import pt.isel.http_api.model.handleChannelFailure
import pt.isel.http_api.model.handleChannelInvitationFailure


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
}

