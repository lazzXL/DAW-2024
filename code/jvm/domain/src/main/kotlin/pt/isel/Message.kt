package pt.isel

import java.time.LocalDateTime
import java.util.UUID

//Constant used for validation
const val MAX_MESSAGE_LENGTH = 4000

/**
 * Represents a message in the system
 * @property content the content of the message
 * @property date the date the message was sent
 * @property user the user that sent the message
 * @property channelID the channel where the message was sent
 */
data class Message (
    val id : UInt,
    val content : String,
    val date : LocalDateTime,
    //val user : User,
    //val channel: Channel
    val sender  : Participant
){
    init {
        require(content.length < MAX_MESSAGE_LENGTH)
    }
}
