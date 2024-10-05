package pt.isel

data class Email (val email: String) {
    init {
        require(email.contains("@") &&
                email.contains(".") &&
                email.length > MIN_NAME_LENGTH){
            "$email is not a valid email."
        }
    }
    override fun toString() = email
}