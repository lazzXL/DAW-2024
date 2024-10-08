package pt.isel

import java.time.LocalDateTime
import java.util.*

sealed class MessageError {
    data object UserNotFound: MessageError()
    data object ChannelNotFound: MessageError()

}

class MessageServices(
    private val trxManager: TransactionManager
) {
    fun sendMessage(
        content: String,
        date: LocalDateTime,
        participant: UInt
    ): Either<MessageError, Message> = trxManager.run {
        val messageParticipant = repoParticipant.findById(participant) ?: return@run failure(MessageError.UserNotFound)
        success(repoMessage.sendMessage(content, date, messageParticipant))
    }

    fun getMessages(
        channelID: UInt,
        numOfMessages: UInt
    ): Either<MessageError, List<Message>> = trxManager.run {
        val channel = repoChannel.findById(channelID) ?: return@run failure(MessageError.ChannelNotFound)
        success(repoMessage.getMessages(channel, numOfMessages))
    }
}