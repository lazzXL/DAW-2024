package pt.isel

import org.springframework.security.crypto.bcrypt.BCrypt

class BCryptPasswordEncoder : PasswordEncoder {
    override fun createValidationInformation(password: String): PasswordValidationInfo {

        return PasswordValidationInfo(hash(password))
    }

    fun hash(input: String): String {
        return BCrypt.hashpw(input, BCrypt.gensalt())
    }

    fun matches(input: String, hashed: String): Boolean {
        return BCrypt.checkpw(input, hashed)
    }

    fun encode(input: String): String {
        return BCrypt.hashpw(input, BCrypt.gensalt())
    }

}