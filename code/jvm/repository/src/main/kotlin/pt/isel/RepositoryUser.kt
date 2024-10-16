package pt.isel

import kotlinx.datetime.Instant

interface RepositoryUser : Repository<User>{
    fun createUser(name : String, email : Email, password : PasswordValidationInfo) : User
    fun findByEmail(email : Email) : User?

    fun findByToken(token: String) : User?
    fun findByName(name: String) : User?
    fun createToken(token : Token, maxTokens: Int)
    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?
    fun updateTokenLastUsed(token: Token, now: Instant)
    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}