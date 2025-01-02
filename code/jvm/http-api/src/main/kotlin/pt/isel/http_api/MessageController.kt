package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.isel.*
import pt.isel.http_api.model.SendMessageInput
import pt.isel.http_api.model.handleMessageFailure
import java.util.concurrent.TimeUnit

/**
 * REST controller for the messages.
 * @property messageService the service for the messages.
 */
@RestController
@RequestMapping("/message")
class MessageController(
    private val messageService: MessageServices
) {


    @PostMapping("/send")
    fun sendMessage(@RequestBody messageInput: SendMessageInput, authenticatedUser: AuthenticatedUser): ResponseEntity<Message> {
        return when (val result: Either<MessageError, Message> = messageService.sendMessage(messageInput.content, messageInput.channelId, authenticatedUser.user.id)) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleMessageFailure(result.value)
        }
    }
    @GetMapping("/{channelId}/")
    fun getMessages(@PathVariable channelId: UInt, @RequestParam(required = false) limit : Int?, @RequestParam(required = false) skip : Int?, authenticatedUser: AuthenticatedUser): ResponseEntity<List<Message>> {
        return when (val result: Either<MessageError, List<Message>> = messageService.getMessages(channelId, authenticatedUser.user.id, limit, skip)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleMessageFailure(result.value)
        }
    }

    @GetMapping("/{channelId}/listen")
    fun listen(
        @PathVariable channelId: UInt,
    ): SseEmitter {
        val sseEmitter = SseEmitter(TimeUnit.HOURS.toMillis(1))
        messageService.addEmitter(
            channelId,
            SseUpdatedTimeSlotEmitterAdapter(
                sseEmitter,
            ),
        )
        return sseEmitter
    }

}

