package pt.isel

/**
 * Represents an email.
 * @property email the email string.
 */
@JvmInline
value class Email (val email: String) {
    init {
        require(email.contains("@") &&
                email.contains(".") &&
                email.length > MIN_NAME_LENGTH){
            "$email is not a valid email."
        }
    }
    override fun toString() = email
}