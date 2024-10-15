package pt.isel

import kotlinx.datetime.Instant

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: UInt,
    val createdAt: Instant,
    val lastUsedAt: Instant,
)
