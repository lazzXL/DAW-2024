package pt.isel.http_api.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.*

fun <T> handleChannelFailure(error: ChannelError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ChannelError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is ChannelError.ChannelNameAlreadyExists -> HttpStatus.CONFLICT to "Channel name already exists."
        is ChannelError.ChannelNotPublic -> HttpStatus.FORBIDDEN to "Channel is not public."
        is ChannelError.AdminNotFound -> HttpStatus.NOT_FOUND to "Admin not found."
        is ChannelError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is ChannelError.InvalidInvite -> HttpStatus.BAD_REQUEST to "Invalid invite code."
    }

    return ResponseEntity.status(status).body(message as T)
}

fun <T> handleUserFailure(error: UserError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is UserError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is UserError.EmailNotFound -> HttpStatus.NOT_FOUND to "Email not found."
        is UserError.IncorrectPassword -> HttpStatus.BAD_REQUEST to "Incorrect password."
        is UserError.EmailAlreadyExists -> HttpStatus.CONFLICT to "Email already exists."
        is UserError.PasswordsDoNotMatch -> HttpStatus.BAD_REQUEST to "Passwords do not match."
        is UserError.UsernameAlreadyExists -> HttpStatus.CONFLICT to "Username already exists"
        is UserError.InsecurePassword -> HttpStatus.BAD_REQUEST to "Password is not secure (Must contain upper and lower case and a number)"
    }

    return ResponseEntity.status(status).body(message as T)
}

fun <T> handleMessageFailure(error: MessageError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is MessageError.ParticipantNotFound -> HttpStatus.NOT_FOUND to "Participant not found."
        is MessageError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
    }

    return ResponseEntity.status(status).body(message as T)
}

fun <T> handleChannelInvitationFailure(error: ChannelInvitationError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ChannelInvitationError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is ChannelInvitationError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is ChannelInvitationError.PermissionInvalid -> HttpStatus.BAD_REQUEST to "Permission invalid."
        is ChannelInvitationError.UserNotInChannel -> HttpStatus.BAD_REQUEST to "User not in channel."
    }

    return ResponseEntity.status(status).body(message as T)
}


fun <T> handleRegisterInvitationFailure(error: RegisterInvitationError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is RegisterInvitationError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is RegisterInvitationError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is RegisterInvitationError.PermissionInvalid -> HttpStatus.BAD_REQUEST to "Permission invalid."
        is RegisterInvitationError.UserNotInChannel -> HttpStatus.BAD_REQUEST to "User not in channel."
    }

    return ResponseEntity.status(status).body(message as T)
}
fun <T> handleParticipantFailure(error: ParticipantError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is ParticipantError.ParticipantNotFound -> HttpStatus.NOT_FOUND to "Participant not found."
        is ParticipantError.ChannelNotPublic -> HttpStatus.FORBIDDEN to "Channel not public."
        is ParticipantError.UserNotFound -> HttpStatus.NOT_FOUND to "Participant not found."
        is ParticipantError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found"
        is ParticipantError.InvalidInvite -> HttpStatus.NOT_FOUND to "Invalid Invitation"
    }

    return ResponseEntity.status(status).body(message as T)
}