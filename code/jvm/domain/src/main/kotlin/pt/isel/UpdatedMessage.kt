package pt.isel

import kotlinx.datetime.Instant

sealed interface UpdatedMessage {
    data class TxMessage(
        val id: Long,
        val message: Message,
    ) : UpdatedMessage

    data class KeepAlive(
        val timestamp: Instant,
    ) : UpdatedMessage
}