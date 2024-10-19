package pt.isel

import jakarta.inject.Named
import java.time.LocalDateTime

sealed class MessageError {
    data object ParticipantNotFound: MessageError()
    data object ChannelNotFound: MessageError()
    data object NoWritePermission: MessageError()

}

@Named
class MessageServices(
    private val trxManager: TransactionManager
) {
    fun sendMessage(
        content: String,
        channelId: UInt,
        userId: UInt
    ): Either<MessageError, Message> = trxManager.run {
        val messageParticipant = repoParticipant.isParticipant(channelId,userId) ?: return@run failure(MessageError.ParticipantNotFound)
        if(messageParticipant.permission == Permission.READ_ONLY) return@run failure(MessageError.NoWritePermission)
        success(repoMessage.sendMessage(content, LocalDateTime.now().withNano((LocalDateTime.now().nano / 1000) * 1000), messageParticipant.id))
    }

    fun getMessages(
        channelID: UInt,
        userId: UInt,
        limit: Int? = null,
        skip: Int? = null
    ): Either<MessageError, List<Message>> = trxManager.run {
        val channel = repoChannel.findById(channelID) ?: return@run failure(MessageError.ChannelNotFound)
        repoParticipant.isParticipant(channelID,userId) ?: return@run failure(MessageError.ParticipantNotFound)
        success(repoMessage.getMessages(channel, limit, skip))
    }

}