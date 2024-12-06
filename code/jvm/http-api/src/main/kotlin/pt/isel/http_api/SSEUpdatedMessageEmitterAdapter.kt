package pt.isel.http_api

import org.slf4j.LoggerFactory
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.isel.MessageServices
import pt.isel.UpdatedMessage
import pt.isel.UpdatedMessageEmitter
import java.io.IOException

class SseUpdatedTimeSlotEmitterAdapter(
    private val sseEmitter: SseEmitter,
) : UpdatedMessageEmitter {
    override fun emit(signal: UpdatedMessage) {
        val msg = when (signal) {
            is UpdatedMessage.TxMessage ->
                SseEmitter
                    .event()
                    .id(signal.id.toString())
                    .name("message")
                    .data(signal.message)
            is UpdatedMessage.KeepAlive ->
                SseEmitter.event().comment(signal.timestamp.epochSeconds.toString())
        }
        sseEmitter.send(msg)
    }
    override fun onCompletion(callback: () -> Unit) {
        sseEmitter.onCompletion(callback)
    }
    override fun onError(callback: (Throwable) -> Unit) {
        sseEmitter.onError(callback)
    }

    companion object{
        private val logger = LoggerFactory.getLogger(SseUpdatedTimeSlotEmitterAdapter::class.java)
    }
}