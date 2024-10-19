package pt.isel.http_api

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.*
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.RegistrationInput
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

    val defaultName = "Alice"
    val defaultEmail = Email("Alice@email.com")
    val defaultPassword = "Alice1234"
    val defaultInvitation = UUID.fromString("00000000-0000-0000-0000-000000000000")

    val jdbi =
        Jdbi
            .create(
                PGSimpleDataSource().apply {
                    setURL(Environment.getDbUrl())
                },
            ).configureWithAppRequirements()



    fun cleanup(trxManager: TransactionManager) {
        trxManager.run {
            repoMessage.clear()
            repoParticipant.clear()
            repoChannelInvitation.clear()
            repoChannel.clear()
            repoRegisterInvitation.clear()
            repoUser.clear()
            repoRegisterInvitation.clear()
            repoRegisterInvitation.createInvitation(UUID.fromString("00000000-0000-0000-0000-000000000000"))
            repoRegisterInvitation.createInvitation(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        }
    }



fun createChannelService(trxManager: TransactionManager) = ChannelServices(trxManager)

fun createUserService(
    trxManager: TransactionManager,
    testClock: TestClock,
    tokenTtl: Duration = 30.days,
    tokenRollingTtl: Duration = 30.minutes,
    maxTokensPerUser: Int = 3,
) = UserServices(
    trxManager,
    UsersDomain(
        BCryptPasswordEncoder(),
        Sha256TokenEncoder(),
        UsersDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = tokenTtl,
            tokenRollingTtl,
            maxTokensPerUser = maxTokensPerUser,
        ),
    ),
    testClock,
)

fun createMessageService(trxManager: TransactionManager) = MessageServices(trxManager)

fun createRegisterInvitationService(trxManager: TransactionManager) = RegisterInvitationServices(trxManager)

fun createChannelInvitationService(trxManager: TransactionManager) = ChannelInvitationServices(trxManager)

fun createParticipantService(trxManager: TransactionManager) = ParticipantServices(trxManager)

fun createUser(trxManager: TransactionManager) : User {
    val controllerUser = UserController(createUserService(trxManager, TestClock()))
    val user = controllerUser.register(RegistrationInput(defaultInvitation, defaultName, defaultEmail, defaultPassword))
    return user.body!!
}

fun getToken(trxManager: TransactionManager) : String {
    val controllerUser = UserController(createUserService(trxManager, TestClock()))
    val login = controllerUser.login(LoginInput(defaultName, defaultPassword))
    return login.body!!.token
}




