import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.RegisterInvitationController
import pt.isel.http_api.UserController
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.RegistrationInput
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UserControllerTest {

	private val defaultName = "Bob Alice"
	private val defaultEmail = "BobAlice@email.com"
	private val defaultPassword = "BobAlice1234"
	companion object {
		private val jdbi =
			Jdbi
				.create(
					PGSimpleDataSource().apply {
						setURL(Environment.getDbUrl())
					},
				).configureWithAppRequirements()

		@JvmStatic
		fun transactionManagers(): Stream<TransactionManager> =
			Stream.of(
				TransactionManagerJdbi(jdbi).also { cleanup(it) },
			)

		private fun cleanup(trxManager: TransactionManager) {
			trxManager.run {
				repoUser.clear()
			}
		}

		private fun createUserService(
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
	}

	private fun createUser(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))
		val registerInvitation = RegisterInvitationController(RegisterInvitationServices(trxManager))

		val invitation = registerInvitation.createInvitation()

		controllerUser.register(RegistrationInput(invitation.body!!.code, defaultName, defaultEmail, defaultPassword))
	}
	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test create an user`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))
		val registerInvitation = RegisterInvitationController(RegisterInvitationServices(trxManager))


		val invitation = registerInvitation.createInvitation()

		controllerUser.register(RegistrationInput(invitation.body!!.code, defaultName, defaultEmail, defaultPassword)).let { resp ->
				assertEquals(HttpStatus.CREATED, resp.statusCode)
				assertNotNull(resp.body)
				assertEquals(defaultName, resp.body!!.name)
				assertEquals(defaultEmail, resp.body!!.email.email)
			}
		cleanup(trxManager)
	}


	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test login`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		createUser(trxManager)

		val login = controllerUser.login(LoginInput(defaultName, defaultPassword))

		assertEquals(HttpStatus.CREATED, login.statusCode)
		assertNotNull(login.body)
		assertNotNull(login.body!!.token)
		assertEquals(256 / 8, login.body!!.token.length)

		cleanup(trxManager)
	}

	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test login with wrong password`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		createUser(trxManager)

		val login = controllerUser.login(LoginInput(defaultName, "wrongPassword"))

		assertEquals(HttpStatus.BAD_REQUEST, login.statusCode)
		assertNotNull(login.body)

		//TODO see if returning with login output is correct

		cleanup(trxManager)
	}


}