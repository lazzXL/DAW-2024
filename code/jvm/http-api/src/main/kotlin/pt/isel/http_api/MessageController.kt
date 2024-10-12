package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.SendMessageInput
import pt.isel.http_api.model.handleMessageFailure


@RestController
@RequestMapping("/message")
class MessageController(
    private val messageService: MessageServices
) {
    @PostMapping("/send")
    fun sendMessage(@RequestBody messageInput: SendMessageInput): ResponseEntity<Message> {
        return when (val result: Either<MessageError, Message> = messageService.sendMessage(messageInput.content, messageInput.date, messageInput.participantId)) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleMessageFailure(result.value)
        }
    }

    @GetMapping("/{channelId}/{numOfMessages}")
    fun getMessages(@PathVariable channelId: UInt, @PathVariable numOfMessages: UInt): ResponseEntity<List<Message>> {
        return when (val result: Either<MessageError, List<Message>> = messageService.getMessages(channelId, numOfMessages)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(result.value)
            is Failure -> handleMessageFailure(result.value)
        }
    }

}

