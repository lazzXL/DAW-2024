package pt.isel

import java.util.UUID

const val MIN_NAME_LENGTH = 4
const val MAX_NAME_LENGTH = 40


data class User (
    val id : UUID,
    val name : String,
    val email : Email,
    val password : String,
    val channels : Set<UUID>
){
    init {
        require(name.length in MIN_NAME_LENGTH  .. MAX_NAME_LENGTH &&
                name.isNotBlank()
        )
    }
}

