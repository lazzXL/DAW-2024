package pt.isel.mem

import kotlinx.datetime.Instant
import pt.isel.*

class RepositoryUserInMem : RepositoryUser {
    private val users = mutableListOf<User>()
    private val tokens = mutableListOf<Token>()

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

    override fun createUser(name: String, email: Email, password: PasswordValidationInfo): User {
        val user =User(users.count().toUInt(), name, email, password)
        users.add(user)
        return user
    }

    override fun findByEmail(email: Email): User? =
        users.firstOrNull { it.email == email }

    override fun findByName(name: String): User? =
        users.firstOrNull { it.name == name }


    override fun createToken(
        token: Token,
        maxTokens: Int
    ) {
        val nrOfTokens = tokens.count { it.userId == token.userId }

        if (nrOfTokens >= maxTokens) {
            tokens
                .filter { it.userId == token.userId }
                .minByOrNull { it.lastUsedAt }!!
                .also { tk -> tokens.removeIf { it.tokenValidationInfo == tk.tokenValidationInfo } }
        }
        tokens.add(token)
    }
    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        tokens.firstOrNull { it.tokenValidationInfo == tokenValidationInfo }?.let {
            val user = findById(it.userId)
            requireNotNull(user)
            user to it
        }

    override fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    ) {
        tokens.removeIf { it.tokenValidationInfo == token.tokenValidationInfo }
        tokens.add(token)
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        val count = tokens.count { it.tokenValidationInfo == tokenValidationInfo }
        tokens.removeAll { it.tokenValidationInfo == tokenValidationInfo }
        return count
    }
}