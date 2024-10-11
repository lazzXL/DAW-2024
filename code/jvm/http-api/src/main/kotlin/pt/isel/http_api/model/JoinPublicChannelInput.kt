package pt.isel.http_api.model

import java.util.*


data class JoinPublicChannelInput(
       val userId: UInt,
        val channelId: UInt
)