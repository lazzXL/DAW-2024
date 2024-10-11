package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.RegistrationInput
import java.util.UUID



@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServices
) {

    @PostMapping("/login")
    fun login(@RequestBody userInput: LoginInput): ResponseEntity<UUID> {
        val email = Email(userInput.email)
        val result: Either<UserError, UUID> = userService.login(
            email,
            userInput.password
        )

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> TODO()

        }
    }
    @PostMapping("/register")
    fun register(@RequestBody registrationInput: RegistrationInput): ResponseEntity<User> {
        val email = Email(registrationInput.email)
        val result: Either<UserError, User> = userService.registration(
            registrationInput.name,
            email,
            registrationInput.password
        )

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> TODO()
        }
    }
}

