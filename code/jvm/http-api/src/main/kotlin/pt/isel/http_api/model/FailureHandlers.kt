package pt.isel.http_api.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.*

/**
 * Handles the failure of a channel operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
 */
fun <T> handleChannelFailure(error: ChannelError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ChannelError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is ChannelError.ChannelNameAlreadyExists -> HttpStatus.CONFLICT to "Channel name already exists."
        is ChannelError.ChannelNotPublic -> HttpStatus.FORBIDDEN to "Channel is not public."
        ChannelError.NoChannelChangesProvided -> TODO()
        ChannelError.UserNotAdmin -> TODO()
    }

    return ResponseEntity.status(status).body(message as T)
}

/**
 * Handles the failure of a user operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
 */
fun <T> handleUserFailure(error: UserError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is UserError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is UserError.EmailNotFound -> HttpStatus.NOT_FOUND to "Email not found."
        is UserError.IncorrectPassword -> HttpStatus.BAD_REQUEST to "Incorrect password."
        is UserError.EmailAlreadyExists -> HttpStatus.CONFLICT to "Email already exists."
        is UserError.PasswordsDoNotMatch -> HttpStatus.BAD_REQUEST to "Passwords do not match."
        is UserError.UsernameAlreadyExists -> HttpStatus.CONFLICT to "Username already exists"
        is UserError.InsecurePassword -> HttpStatus.BAD_REQUEST to "Password is not secure (Must contain upper and lower case and a number)"
        is UserError.InvitationDoesNotExist -> HttpStatus.NOT_FOUND to "Invitation not found."
    }

    return ResponseEntity.status(status).body(message as T)
}
/**
 * Handles the failure of a message operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
 */
fun <T> handleMessageFailure(error: MessageError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is MessageError.ParticipantNotFound -> HttpStatus.NOT_FOUND to "Participant not found."
        is MessageError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is MessageError.NoWritePermission -> HttpStatus.UNAUTHORIZED to "User does not have permission to write."
    }

    return ResponseEntity.status(status).body(message as T)
}
/**
 * Handles the failure of a channel invitation operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
 */
fun <T> handleChannelInvitationFailure(error: ChannelInvitationError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ChannelInvitationError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is ChannelInvitationError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is ChannelInvitationError.PermissionInvalid -> HttpStatus.BAD_REQUEST to "Permission invalid."
        is ChannelInvitationError.UserIsNotAdmin -> HttpStatus.FORBIDDEN to "Only admin can create invitations."
        is ChannelInvitationError.InvitationNotFound -> HttpStatus.NOT_FOUND to "Invitation not found."
    }

    return ResponseEntity.status(status).body(message as T)
}

/**
 * Handles the failure of a register invitation operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
*/
fun <T> handleRegisterInvitationFailure(error: RegisterInvitationError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is RegisterInvitationError.InvitationNotFound -> HttpStatus.NOT_FOUND to "Invitation not found."
    }

    return ResponseEntity.status(status).body(message as T)
}
/**
 * Handles the failure of a participant operation.
 * @param error the error that occurred.
 * @return a response entity with the error message.
 */
fun <T> handleParticipantFailure(error: ParticipantError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ParticipantError.ParticipantNotFound -> HttpStatus.NOT_FOUND to "Participant not found."
        is ParticipantError.ChannelNotPublic -> HttpStatus.FORBIDDEN to "Channel not public."
        is ParticipantError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found"
        is ParticipantError.InvalidInvite -> HttpStatus.NOT_FOUND to "Invalid Invitation"
        is ParticipantError.UserAlreadyInChannel ->  HttpStatus.CONFLICT to "User already in channel."
    }

    return ResponseEntity.status(status).body(message as T)
}