package pt.isel.mem

import kotlinx.datetime.Instant
import pt.isel.*

class RepositoryUserInMem : RepositoryUser {
    private val users = mutableListOf<User>()
    //private val tokens = mutableListOf<Token>()

    override fun findById(id: UInt): User? =
        users.firstOrNull{it.id == id}

    override fun findAll(): List<User> =
        users.toList()

    override fun save(entity: User) {
        users.removeIf{it.id == entity.id}
        users.add(entity)
    }

    override fun deleteById(id: UInt) {
        users.removeIf{it.id == id}
    }

    override fun clear() =
        users.clear()

    override fun createUser(name: String, email: Email, password: PasswordValidationInfo): User =
        User(users.count().toUInt(), name, email, password)

    override fun findByEmail(email: Email): User? =
        users.firstOrNull { it.email == email }

    override fun findByName(name: String): User? =
        users.firstOrNull { it.name == name }

    override fun createToken(
        token: Token,
       maxTokens: Int,
    ) {
        TODO()
    }
//        val nrOfTokens = tokens.count { it.userId == token.userId }
//
//        if (nrOfTokens >= maxTokens) {
//            tokens
//                .filter { it.userId == token.userId }
//                .minByOrNull { it.lastUsedAt }!!
//                .also { tk -> tokens.removeIf { it.tokenValidationInfo == tk.tokenValidationInfo } }
//        }
//        tokens.add(token)

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        TODO("Not yet implemented")
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        TODO("Not yet implemented")
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        TODO("Not yet implemented")
    }
}