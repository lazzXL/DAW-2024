package pt.isel.http_api.model

import java.time.LocalDateTime

data class SendMessageInput(
    val content: String,
    //val date: LocalDateTime,
    val participantId: UInt
)