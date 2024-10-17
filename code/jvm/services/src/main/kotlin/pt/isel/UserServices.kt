package pt.isel

import jakarta.inject.Named
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

sealed class UserError {

    data object UserNotFound : UserError()
    data object EmailNotFound : UserError()
    data object IncorrectPassword : UserError()
    data object EmailAlreadyExists: UserError()
    data object PasswordsDoNotMatch: UserError()
    data object UsernameAlreadyExists: UserError()
    data object InsecurePassword: UserError()
    data object InvitatioDoesNotExist: UserError()

}

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant,
)


@Named
class UserServices(
    private val trxManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
){
    //login
    fun login(
        name : String,
        password: String
    ) : Either<UserError, TokenExternalInfo> = trxManager.run {
        val user = repoUser.findByName(name) ?: return@run failure(UserError.UserNotFound)
        if (!usersDomain.validatePassword(password,user.password))
            return@run failure(UserError.IncorrectPassword)
        val tokenValue = usersDomain.generateTokenValue()
        val now = clock.now()
        val newToken =
            Token(
                usersDomain.createTokenValidationInformation(tokenValue),
                user.id,
                createdAt = now,
                lastUsedAt = now,
            )
        repoUser.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
        Either.Right(
            TokenExternalInfo(
                tokenValue,
                usersDomain.getTokenExpiration(newToken),
            )
        )
    }
    //registration
    fun registration(
        code: UUID,
        email : Email,
        name: String,
        password: String,
    ) : Either<UserError, User> {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserError.InsecurePassword)
        }
        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)
        return trxManager.run {
            val invite = repoRegisterInvitation.findByCode(code)
                ?: return@run failure(UserError.InvitatioDoesNotExist)
            if (repoUser.findByEmail(email) != null)
                return@run failure(UserError.EmailAlreadyExists)
            if (repoUser.findByName(name) != null)
                return@run failure(UserError.UsernameAlreadyExists)
            val user = repoUser.createUser(name, email, passwordValidationInfo)
            repoRegisterInvitation.deleteById(invite.id)
            success(user)
        }
    }

    /*
    //change password
    fun changePassword(
        email : Email,
        password : String,
        newPassword : String
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findByEmail(email) ?: return@run failure(UserError.EmailNotFound)
        if(user.password != password) return@run failure(UserError.PasswordsDoNotMatch)
        val updatedUser = user.copy(password = newPassword)
        repoUser.save(updatedUser)
        success(updatedUser)
    }


     */
    //change email
    fun changeEmail(
        email : Email,
        newEmail : Email
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findByEmail(email) ?: return@run failure(UserError.EmailNotFound)
        if (repoUser.findByEmail(newEmail) != null)
            return@run failure(UserError.EmailAlreadyExists)
        val updatedUser = user.copy(email = newEmail)
        repoUser.save(updatedUser)
        success(updatedUser)
    }
    //change username
    fun changeUsername(
        userId : UInt,
        newUsername : String
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        if (repoUser.findByName(newUsername) != null)
            return@run failure(UserError.UsernameAlreadyExists)
        val updatedUser = user.copy(name = newUsername)
        repoUser.save(updatedUser)
        success(updatedUser)
    }
    //get user info
    fun getUserInfo(
        userId : UInt
    ) : Either<UserError, User> = trxManager.run {
        val user = repoUser.findById(userId) ?: return@run failure(UserError.UserNotFound)
        success(user)
    }

    fun getUserByToken(token: String): Either<UserError, User> = trxManager.run {
        if (!usersDomain.canBeToken(token)) {
            return@run failure(UserError.EmailAlreadyExists)
        }
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        val userAndToken: Pair<User, Token>? = repoUser.getTokenByTokenValidationInfo(tokenValidationInfo)
        if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
            repoUser.updateTokenLastUsed(userAndToken.second, clock.now())
            success(userAndToken.first)
        } else {
            failure(UserError.UserNotFound)
        }
    }
}