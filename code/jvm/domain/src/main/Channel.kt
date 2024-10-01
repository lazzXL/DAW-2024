
import java.util.*
import kotlin.collections.List

const val MAX_DESCRIPTION_LENGTH = 400

data class Channel (
    val id : UUID,
    val name : String,
    val participants : Set<UUID>,
    val admins : Set<UUID>,
    val description : String,
    val message: List<Message>,
    val visibility: Visibility
){
    init {
        require(name.length in MIN_NAME_LENGTH  .. MAX_NAME_LENGTH &&
                name.isNotBlank() &&
                description.length < MAX_DESCRIPTION_LENGTH
        )
    }
}

enum class Visibility{
    PUBLIC, PRIVATE
}