package pt.isel

/**
 * Interface for token encoding.
 */
interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
