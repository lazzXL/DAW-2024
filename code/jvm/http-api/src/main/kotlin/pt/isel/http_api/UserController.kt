package pt.isel.http_api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.*
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.LoginOutput
import pt.isel.http_api.model.RegistrationInput
import pt.isel.http_api.model.handleUserFailure


/**
 * REST Controller for managing users.
 * @property userService the services for the user.
 */
@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserServices
) {
    /**
     * Logs in a user.
     * @param userInput the input for login.
     * @return the response entity.
     */
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
    /**
     * Registers a user.
     * @param registrationInput the input for registration.
     * @return the response entity.
     */
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

    @GetMapping("/findByToken")
    fun findByToken(authenticatedUser: AuthenticatedUser) : ResponseEntity<User> {
        return ResponseEntity.status(HttpStatus.OK).body(authenticatedUser.user)
    }
}