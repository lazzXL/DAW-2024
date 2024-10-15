package pt.isel

interface PasswordEncoder {
    fun createValidationInformation(password: String): PasswordValidationInfo

    fun matches(password: String, hashed: String): Boolean

    fun encode(password: String): String


}