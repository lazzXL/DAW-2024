package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*

import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.JoinChannelViaInviteInput
import pt.isel.http_api.model.JoinPublicChannelInput
import pt.isel.http_api.model.handleChannelFailure


@RestController
@RequestMapping("/channel")
class ChannelController(
    private val channelServices: ChannelServices
) {

    @GetMapping("/{channelId}")
    fun getChannel(@PathVariable channelId : UInt): ResponseEntity<Channel> {
        return when (val result: Either<ChannelError, Channel> = channelServices.getChannel(channelId)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }

    @GetMapping("/joined/{userId}")
    fun getJoinedChannels(@PathVariable userId : UInt): ResponseEntity<List<Channel>> {
        return when (val result: Either<ChannelError, List<Channel>> = channelServices.getJoinedChannels(userId)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }

//    @PostMapping("/join-invite")
//    fun joinChannelByInvite(@RequestBody joinInput : JoinChannelViaInviteInput): ResponseEntity<Any> {
//        return when (val result: Either<ChannelError, Channel> = channelServices.joinChannelByInvite(joinInput.userId, joinInput.code)) {
//            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
//            is Failure -> handleChannelFailure(result.value)
//        }
//    }

//    @PostMapping("/join")
//    fun joinPublicChannel(@RequestBody joinInput : JoinPublicChannelInput): ResponseEntity<Any> {
//        return when (val result: Either<ChannelError, Channel> = channelServices.joinPublicChannel(joinInput.userId, joinInput.channelId) ) {
//            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
//            is Failure -> handleChannelFailure(result.value)
//        }
//    }

    @PostMapping("/create")
    fun createChannel(@RequestBody createInput : CreateChannelInput): ResponseEntity<Any> {
        val visibility = if (createInput.isPublic) Visibility.PUBLIC else Visibility.PRIVATE
        val result: Either<ChannelError, Channel> = channelServices.createChannel(createInput.name, createInput.description, createInput.adminId.toUInt(), visibility)

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }

//    @DeleteMapping("/leave/{userId}/{channelId}")
//    fun leaveChannel(@PathVariable userId : UInt, @PathVariable channelId : UInt): ResponseEntity<Channel> {
//        return when (val result: Either<ChannelError, Channel> = channelServices.leaveChannel(userId, channelId)) {
//            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
//            is Failure -> handleChannelFailure(result.value)
//        }
//    }

}

