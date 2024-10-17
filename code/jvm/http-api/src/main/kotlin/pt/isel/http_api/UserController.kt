package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.*
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.LoginOutput
import pt.isel.http_api.model.RegistrationInput
import pt.isel.http_api.model.handleUserFailure



@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServices
) {

    @PostMapping("/login")
    fun login(@RequestBody userInput: LoginInput): ResponseEntity<LoginOutput> {
        val result: Either<UserError, TokenExternalInfo> = userService.login(
            userInput.name,
            userInput.password
        )

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(LoginOutput(result.value.tokenValue))
            is Failure -> handleUserFailure(result.value)

        }
    }
    @PostMapping("/register")
    fun register(@RequestBody registrationInput: RegistrationInput): ResponseEntity<User> {
        val result: Either<UserError, User> = userService.registration(
            registrationInput.invitation,
            registrationInput.email,
            registrationInput.name,
            registrationInput.password
        )

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> handleUserFailure(result.value)
        }
    }
}