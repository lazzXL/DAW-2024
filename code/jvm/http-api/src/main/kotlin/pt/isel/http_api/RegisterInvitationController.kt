package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.CreateInvitationInput
import pt.isel.http_api.model.handleRegisterInvitationFailure
import java.util.*

@RestController
@RequestMapping("/registerInvitation")
class RegisterInvitationController (
    private val invitationService: RegisterInvitationServices
) {
    @PostMapping("/create")
    fun createInvitation(@RequestBody invitationInput: CreateInvitationInput): ResponseEntity<RegisterInvitation> {
        return when (val result: Either<RegisterInvitationError, RegisterInvitation> = invitationService.createInvitation( UUID.randomUUID())) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleRegisterInvitationFailure(result.value)
        }

    }
}