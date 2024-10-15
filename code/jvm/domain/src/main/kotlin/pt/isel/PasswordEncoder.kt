package pt.isel

interface PasswordEncoder {
    fun createValidationInformation(password: String): PasswordValidationInfo

    fun matches(password: String, validationInfo: PasswordValidationInfo): Boolean

    fun encode(password: String): String


}