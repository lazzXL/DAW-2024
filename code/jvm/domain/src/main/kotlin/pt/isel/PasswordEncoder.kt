package pt.isel

/**
 * Interface for password encoding.
 */
interface PasswordEncoder {
    fun createValidationInformation(password: String): PasswordValidationInfo

    fun matches(password: String, hashed: String): Boolean

    fun encode(password: String): String


}