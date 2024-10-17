package pt.isel

import jakarta.inject.Named
import java.time.LocalDateTime

sealed class MessageError {
    data object ParticipantNotFound: MessageError()
    data object ChannelNotFound: MessageError()

}

@Named
class MessageServices(
    private val trxManager: TransactionManager
) {
    fun sendMessage(
        content: String,
        //date: LocalDateTime,
        participant: UInt
    ): Either<MessageError, Message> = trxManager.run {
        val messageParticipant = repoParticipant.findById(participant) ?: return@run failure(MessageError.ParticipantNotFound)
        success(repoMessage.sendMessage(content, LocalDateTime.now(), messageParticipant.id))
    }

    fun getMessages(
        channelID: UInt,
        numOfMessages: UInt
    ): Either<MessageError, List<Message>> = trxManager.run {
        val channel = repoChannel.findById(channelID) ?: return@run failure(MessageError.ChannelNotFound)
        success(repoMessage.getMessages(channel, numOfMessages))
    }

}