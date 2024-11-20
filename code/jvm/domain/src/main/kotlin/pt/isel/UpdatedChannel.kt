package pt.isel

import kotlinx.datetime.Instant

sealed interface UpdatedChannel {
    data class Message(
        val id: Long,
        val channel: Channel,
    ) : UpdatedChannel

    data class KeepAlive(
        val timestamp: Instant,
    ) : UpdatedChannel
}