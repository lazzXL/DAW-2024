package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*

import pt.isel.http_api.model.CreateChannelInput
import pt.isel.http_api.model.handleChannelFailure

/**
 * REST controller for managing channels
 * @property channelServices the services for the channel.
 */
@RestController
@RequestMapping("/channel")
class ChannelController(
    private val channelServices: ChannelServices
) {


    /**
     * Gets a channel by its id.
     * @param channelId the id of the channel.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @GetMapping("/{channelId}")
    fun getChannel(@PathVariable channelId : UInt, authenticatedUser: AuthenticatedUser): ResponseEntity<Channel> {
        return when (val result: Either<ChannelError, Channel> = channelServices.getChannel(channelId,authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }
    /**
     * Gets all channels that the user has joined.
     * @param name the name of the channel.
     * @param limit the number of channels to return.
     * @param skip the number of channels to skip.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @GetMapping("/joined") // Verified
    fun getJoinedChannels(@RequestParam(required = false) name: String?, @RequestParam(required = false) limit : Int?, @RequestParam(required = false) skip : Int?, authenticatedUser: AuthenticatedUser): ResponseEntity<List<Channel>> {
        val channelName = name.takeIf { !it.isNullOrBlank() }
        return when (val result: Either<ChannelError, List<Channel>> = channelServices.getJoinedChannels(authenticatedUser.user.id,channelName, limit, skip)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }
    /**
     * Gets all public channels (restricted by pagination).
     * @param name the name of the channel.
     * @param limit the number of channels to return.
     * @param skip the number of channels to skip.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @GetMapping("/public") // Verified
    fun getPublicChannels(@RequestParam(required = false) name: String?, @RequestParam(required = false) limit : Int?, @RequestParam(required = false) skip : Int?, authenticatedUser: AuthenticatedUser): ResponseEntity<List<Channel>> {
        val channelName = name.takeIf { !it.isNullOrBlank() }
        return when (val result: Either<ChannelError, List<Channel>> = channelServices.getPublicChannels(channelName, limit, skip)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }


    /**
     * Creates a channel.
     * @param createInput the input model for creating a channel.
     * @param authenticatedUser the authenticated user.
     * @return the response entity.
     */
    @PostMapping("/create")
    fun createChannel(@RequestBody createInput : CreateChannelInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Channel> {
        val visibility = if (createInput.isPublic) Visibility.PUBLIC else Visibility.PRIVATE
        val result: Either<ChannelError, Channel> = channelServices.createChannel(createInput.name, createInput.description, authenticatedUser.user, visibility)

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleChannelFailure(result.value)
        }
    }



}

