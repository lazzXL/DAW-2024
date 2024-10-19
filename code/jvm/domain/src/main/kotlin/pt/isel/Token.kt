package pt.isel

import kotlinx.datetime.Instant

/**
 * Represents a token.
 * @property tokenValidationInfo the token validation information.
 * @property userId the id of the user.
 * @property createdAt the creation date of the token.
 * @property lastUsedAt the last usage date of the token.
 */
class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: UInt,
    val createdAt: Instant,
    val lastUsedAt: Instant,
)
