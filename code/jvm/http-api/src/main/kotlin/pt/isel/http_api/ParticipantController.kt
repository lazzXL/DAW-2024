package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*
import pt.isel.http_api.model.JoinChannelViaInviteInput
import pt.isel.http_api.model.JoinPublicChannelInput
import pt.isel.http_api.model.handleParticipantFailure

/**
 * REST Controller for managing participant.
 * @property participantServices the services for the participant.
 */
@RestController
@RequestMapping("/participant")
class ParticipantController(
    private val participantServices: ParticipantServices
) {
    /**
     * Join a channel by invite.
     * @param joinInput the input for joining a channel by invite.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @PostMapping("/join-invite")
    fun joinChannelByInvite(@RequestBody joinInput : JoinChannelViaInviteInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Participant> = participantServices.joinChannelByInvite(authenticatedUser.user, joinInput.code)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }
    /**
     * Join a public channel.
     * @param joinInput the input for joining a public channel.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @PostMapping("/join") // Verified
    fun joinPublicChannel(@RequestBody joinInput : JoinPublicChannelInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Participant> = participantServices.joinPublicChannel(authenticatedUser.user, joinInput.channelId) ) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }

    /**
     * Leaves a channel.
     * @param channelId the channel id.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @DeleteMapping("/leave/{channelId}")
    fun leaveChannel(@PathVariable channelId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, Unit> = participantServices.leaveChannel(channelId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }

    @GetMapping("/channel/{channelId}")
    fun getParticipantsFromChannel(@PathVariable channelId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        return when (val result: Either<ParticipantError, List<Participant>> = participantServices.getParticipantsFromChannel(channelId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleParticipantFailure(result.value)
        }
    }
}