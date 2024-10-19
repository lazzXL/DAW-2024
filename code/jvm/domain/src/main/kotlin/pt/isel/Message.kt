package pt.isel

import java.time.LocalDateTime

//Constant used for validation
const val MAX_MESSAGE_LENGTH = 4000

/**
 * Represents a message in the system
 * @property id message identifier
 * @property content the content of the message
 * @property date the date the message was sent
 * @property sender id of the participant that sent the message
 */
data class Message (
    val id : UInt,
    val content : String,
    val date : LocalDateTime,
    val sender  : UInt
){
    init {
        require(content.length < MAX_MESSAGE_LENGTH)
    }
}
