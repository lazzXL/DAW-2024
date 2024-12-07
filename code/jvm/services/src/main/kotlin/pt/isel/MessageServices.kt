package pt.isel

import jakarta.annotation.PreDestroy
import jakarta.inject.Named
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.util.concurrent.TimeUnit

sealed class MessageError {
    data object ParticipantNotFound: MessageError()
    data object ChannelNotFound: MessageError()
    data object NoWritePermission: MessageError()

}

@Named
class MessageServices(
    private val trxManager: TransactionManager
) {
    // Important: mutable state on a singleton service
    private val listeners = mutableMapOf<UInt, List<UpdatedMessageEmitter>>()
    private var currentId = 0L
    private val lock = ReentrantLock()

    // A scheduler to send the periodic keep-alive events
    private val scheduler: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1).also {
            it.scheduleAtFixedRate({ keepAlive() }, 2, 2, TimeUnit.SECONDS)
        }
    @PreDestroy
    fun shutdown() {
        logger.info("shutting down")
        scheduler.shutdown()
    }
    fun addEmitter(
        channelId: UInt,
        listener: UpdatedMessageEmitter,
    ) = lock.withLock {
        val ch =
            trxManager.run {
                repoChannel.findById(channelId)
            }
        requireNotNull(ch)
        logger.info("adding listener")
        val oldListeners = listeners.getOrDefault(channelId, emptyList())
        listeners[channelId] = oldListeners + listener
        listener.onCompletion {
            logger.info("onCompletion")
            removeEmitter(channelId, listener)
        }
        listener.onError {
            logger.info("onError")
            removeEmitter(channelId, listener)
        }
        listener
    }
    private fun removeEmitter(
        ch: UInt,
        listener: UpdatedMessageEmitter,
    ) = lock.withLock {
        logger.info("removing listener")
        val oldListeners = listeners[ch]
        requireNotNull(oldListeners)
        logger.info("removing listener")
        listeners[ch] = oldListeners - listener
    }

    private fun keepAlive() =
        lock.withLock {
            logger.info("keepAlive, sending to {} listeners", listeners.values.flatten().size)
            val signal = UpdatedMessage.KeepAlive(Clock.System.now())
            listeners.values.flatten().forEach {
                try {
                    it.emit(signal)
                } catch (ex: Exception) {
                    logger.info("Exception while sending keepAlive signal - {}", ex.message)
                }
            }
        }
    private fun sendEventToAll(
        ch: UInt,
        signal: UpdatedMessage,
    ) {
        listeners[ch]?.forEach {
            try {
                it.emit(signal)
            } catch (ex: Exception) {
                logger.info("Exception while sending Message signal - {}", ex.message)
            }
        }
    }

    fun sendMessage(
        content: String,
        channelId: UInt,
        userId: UInt
    ): Either<MessageError, Message> = trxManager.run {
        val messageParticipant = repoParticipant.isParticipant(channelId,userId) ?: return@run failure(MessageError.ParticipantNotFound)
        if(messageParticipant.permission == Permission.READ_ONLY) return@run failure(MessageError.NoWritePermission)
        val message = repoMessage.sendMessage(content, LocalDateTime.now().withNano((LocalDateTime.now().nano / 1000) * 1000), messageParticipant.id)
        sendEventToAll(channelId, UpdatedMessage.TxMessage(++currentId, message))
        success(message)
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


    companion object {
        private val logger = LoggerFactory.getLogger(MessageServices::class.java)
    }
}