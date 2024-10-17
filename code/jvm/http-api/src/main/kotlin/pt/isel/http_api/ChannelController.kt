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
    // TODO: Missing: updateChannel, deleteChannel, paging in the List returning functions
    @GetMapping("/{channelId}") // Verified
    fun getChannel(@PathVariable channelId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Channel> {
        return when (val result: Either<ChannelError, Channel> = channelServices.getChannel(channelId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }

    @GetMapping("/joined") // Verified
    fun getJoinedChannels(@RequestParam(required = false) name: String?, authenticatedUser: AuthenticatedUser): ResponseEntity<List<Channel>> {
        val channelName = name.takeIf { !it.isNullOrBlank() }
        return when (val result: Either<ChannelError, List<Channel>> = channelServices.getJoinedChannels(authenticatedUser.user.id,channelName)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }

    @GetMapping("/public") // Verified
    fun getPublicChannels(@RequestParam(required = false) name: String?, authenticatedUser: AuthenticatedUser): ResponseEntity<List<Channel>> {
        val channelName = name.takeIf { !it.isNullOrBlank() }
        return when (val result: Either<ChannelError, List<Channel>> = channelServices.getPublicChannels(channelName)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }



    @PostMapping("/create") // Verified
    fun createChannel(@RequestBody createInput : CreateChannelInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Any> {
        val visibility = if (createInput.isPublic) Visibility.PUBLIC else Visibility.PRIVATE
        val result: Either<ChannelError, Channel> = channelServices.createChannel(createInput.name, createInput.description, authenticatedUser.user, visibility)

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }



}

