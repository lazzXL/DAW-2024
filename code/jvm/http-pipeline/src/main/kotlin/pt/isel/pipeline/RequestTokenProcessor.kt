package pt.isel.pipeline

import org.springframework.stereotype.Component
import pt.isel.*

@Component
class RequestTokenProcessor(
    val usersService: UserServices,
) {
    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return when (val user = usersService.getUserByToken(parts[1])) {
            is Failure -> null
            is Success -> AuthenticatedUser(user.value, parts[1])
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}
