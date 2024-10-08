package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.Either
import pt.isel.Success
import pt.isel.UserError
import pt.isel.UserServices
import pt.isel.http_api.model.UserInput


@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserServices
) {
    @PostMapping
    fun createUser(@RequestBody userInput: UserInput): ResponseEntity<Unit> {

    }
}