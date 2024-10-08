package pt.isel

/**
 * Represents a channel permission
 */
enum class Permission {
    READ_WRITE, READ_ONLY
}


fun String.parseToPermission(): Permission? =
    when(this){
        "READ_ONLY" -> Permission.READ_ONLY
        "READ_WRITE" -> Permission.READ_WRITE
        else -> null
    }
