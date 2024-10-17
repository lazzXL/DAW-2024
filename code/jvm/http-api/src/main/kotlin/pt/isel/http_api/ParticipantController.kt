package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*
import pt.isel.http_api.model.JoinChannelViaInviteInput
import pt.isel.http_api.model.JoinPublicChannelInput
import pt.isel.http_api.model.handleParticipantFailure

@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val participantServices: ParticipantServices
) {
    @PostMapping("/join-invite") // Verified
    fun joinChannelByInvite(@RequestBody joinInput : JoinChannelViaInviteInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Participant> = participantServices.joinChannelByInvite(authenticatedUser.user, joinInput.code)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }

    @PostMapping("/join") // Verified
    fun joinPublicChannel(@RequestBody joinInput : JoinPublicChannelInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Participant> = participantServices.joinPublicChannel(authenticatedUser.user, joinInput.channelId) ) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }

    //TODO: RETURNING VALUE IS KINDA SCUFFED
    @DeleteMapping("/leave/{channelId}")
    fun leaveChannel(@PathVariable channelId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Unit> = participantServices.leaveChannel(channelId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }
}