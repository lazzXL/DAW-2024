package pt.isel

import java.util.UUID

//Constants used for validation
const val MIN_NAME_LENGTH = 4
const val MAX_NAME_LENGTH = 40

/**
 * Represents a user in the system
 * @property id the unique identifier of the user
 * @property name the name of the user
 * @property email the email of the user
 * @property password the password of the user
 * @property channels the channels the user is in
 */
data class User (
    val id : UInt,
   // val token : UUID,
    val name : String,
    val email : Email,
    val password : PasswordValidationInfo
){
    init {
        require(name.length in MIN_NAME_LENGTH  .. MAX_NAME_LENGTH &&
                name.isNotBlank()
        )
    }
}

