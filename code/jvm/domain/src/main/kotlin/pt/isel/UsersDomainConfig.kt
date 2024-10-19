package pt.isel

import jakarta.inject.Named
import kotlin.time.Duration

/**
 * Represents the configuration of the users domain.
 * @property tokenSizeInBytes the size of the token in bytes.
 * @property tokenTtl the time to live of the token.
 * @property tokenRollingTtl the rolling time to live of the token.
 * @property maxTokensPerUser the maximum number of tokens per user.
 */
@Named
data class UsersDomainConfig(
    val tokenSizeInBytes: Int,
    val tokenTtl: Duration,
    val tokenRollingTtl: Duration,
    val maxTokensPerUser: Int,
) {
    init {
        require(tokenSizeInBytes > 0)
        require(tokenTtl.isPositive())
        require(tokenRollingTtl.isPositive())
        require(maxTokensPerUser > 0)
    }
}
