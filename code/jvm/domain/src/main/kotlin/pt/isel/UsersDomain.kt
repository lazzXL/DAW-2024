package pt.isel

import java.security.SecureRandom
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.Base64.getUrlDecoder
import java.util.Base64.getUrlEncoder

class UsersDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig,
) {
    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean =
        try {
            getUrlDecoder().decode(token).size == config.tokenSizeInBytes
        } catch (ex: IllegalArgumentException) {
            false
        }

     fun validatePassword(
         password: String,
         validationInfo: PasswordValidationInfo
     ) = passwordEncoder
         .matches(password, validationInfo)

     fun createPasswordValidationInformation(password: String) =
         PasswordValidationInfo(
             passwordEncoder.encode(password),
         )

    fun isSafePassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return password.length >= 6 && hasUppercase && hasLowercase && hasDigit
    }

    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }

    fun createTokenValidationInformation(token: String) = tokenEncoder.createValidationInformation(token)

    fun isTokenTimeValid(
        clock: Clock,
        token: Token,
    ): Boolean {
        val expiration = getTokenExpiration(token)
        return clock.now() < expiration
    }

    val maxNumberOfTokensPerUser = config.maxTokensPerUser
}