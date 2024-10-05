package pt.isel

import java.util.*
import kotlin.collections.List

//Constant used for validation
const val MAX_DESCRIPTION_LENGTH = 400

/**
 * Represents a channel in the system
 * @property id the unique identifier of the channel
 * @property name the name of the channel
 * @property admin the user that created the channel
 * @property description a brief description of the channel
 * @property visibility the visibility of the channel
 */
data class Channel (
    val id : UUID,
    val name : String,
    //val participants : Set<UUID>,
    //val admins : Set<UUID>,
    val admin : UUID,
    val description : String,
    //val message: List<Message>,
    val visibility: Visibility
){
    init {
        require(name.length in MIN_NAME_LENGTH  .. MAX_NAME_LENGTH &&
                name.isNotBlank() &&
                description.length < MAX_DESCRIPTION_LENGTH
        )
    }
}

