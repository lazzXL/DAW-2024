package pt.isel

import jakarta.inject.Named
import org.springframework.security.crypto.bcrypt.BCrypt

@Named
class BCryptPasswordEncoder : PasswordEncoder {
    override fun createValidationInformation(password: String): PasswordValidationInfo {
        return PasswordValidationInfo(hash(password))
    }

    private fun hash(input: String): String {
        return BCrypt.hashpw(input, BCrypt.gensalt())
    }

    override fun matches(password: String, hashed: String): Boolean {
        return BCrypt.checkpw(password, hashed)
    }

    override fun encode(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

}