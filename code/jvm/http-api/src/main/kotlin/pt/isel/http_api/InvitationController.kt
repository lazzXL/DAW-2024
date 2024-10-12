package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.CreateInvitationInput
import pt.isel.http_api.model.handleInvitationFailure


@RestController
@RequestMapping("/invitation")
class InvitationController(
    private val invitationService: InvitationServices
) {
     @PostMapping("/create")
    fun createInvitation(@RequestBody invitationInput: CreateInvitationInput): ResponseEntity<Invitation> {
         return when (val result: Either<InvitationError, Invitation> = invitationService.createInvitation(invitationInput.channelID, invitationInput.permission, invitationInput.userId)) {
             is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
             is Failure -> handleInvitationFailure(result.value)
         }

    }
}

