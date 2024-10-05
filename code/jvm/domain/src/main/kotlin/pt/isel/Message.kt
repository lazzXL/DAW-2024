package pt.isel

import java.time.LocalDateTime
import java.util.UUID

const val MAX_MESSAGE_LENGTH = 4000

data class Message (
    val content : String,
    val date : LocalDateTime,
    val user : UUID,
    val channelID: UUID
){
    init {
        require(content.length < MAX_MESSAGE_LENGTH)
    }
}
