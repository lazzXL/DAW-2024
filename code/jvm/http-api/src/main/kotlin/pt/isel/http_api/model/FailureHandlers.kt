package pt.isel.http_api.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.ChannelError
import pt.isel.InvitationError
import pt.isel.MessageError
import pt.isel.UserError

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

fun <T> handleInvitationFailure(error: InvitationError): ResponseEntity<T> {
    val (status, message) = when (error) {
        is InvitationError.ChannelNotFound -> HttpStatus.NOT_FOUND to "Channel not found."
        is InvitationError.UserNotFound -> HttpStatus.NOT_FOUND to "User not found."
        is InvitationError.PermissionInvalid -> HttpStatus.BAD_REQUEST to "Permission invalid."
        is InvitationError.UserNotInChannel -> HttpStatus.BAD_REQUEST to "User not in channel."
    }

    return ResponseEntity.status(status).body(message as T)
}