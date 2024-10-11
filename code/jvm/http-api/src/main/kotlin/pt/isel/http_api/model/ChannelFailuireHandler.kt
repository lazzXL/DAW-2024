package pt.isel.http_api.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.ChannelError

fun <T> handleFailure(error: ChannelError): ResponseEntity<T> {
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