package pt.isel

/**
 * Represents an authenticated user.
 * @property user the user.
 * @property token the token.
 */
class AuthenticatedUser(
    val user: User,
    val token: String,
)
