package pt.isel.http_api

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import pt.isel.*
import pt.isel.http_api.model.LoginInput
import pt.isel.http_api.model.LoginOutput
import pt.isel.http_api.model.RegistrationInput
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UserControllerTests {

	companion object {
		@JvmStatic
		fun transactionManagers(): Stream<TransactionManager> =
			Stream.of(
				TransactionManagerJdbi(jdbi).also { cleanup(it) },
			)
	}
	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test create an user`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		controllerUser.register(RegistrationInput(defaultInvitation, defaultName, defaultEmail, defaultPassword)).let { resp ->
			assertEquals(HttpStatus.CREATED, resp.statusCode)
			assertNotNull(resp.body)
			assertIs<User>(resp.body)
			val user = resp.body as User
			assertEquals(defaultName, user.name)
			assertEquals(defaultEmail, user.email)
		}
	}

	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test login`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		createUser(trxManager)

		val login = controllerUser.login(LoginInput(defaultName, defaultPassword)).let { resp ->
			assertEquals(HttpStatus.CREATED, resp.statusCode)
			assertNotNull(resp.body)
			assertIs<LoginOutput>(resp.body)
		}
	}

	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test login with wrong password`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		createUser(trxManager)

		val login = controllerUser.login(LoginInput(defaultName, "wrongPassword"))

		assertEquals(HttpStatus.BAD_REQUEST, login.statusCode)
	}

	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `test login with username that does not exist`(trxManager: TransactionManager) {
		val controllerUser = UserController(createUserService(trxManager, TestClock()))

		createUser(trxManager)

		val login = controllerUser.login(LoginInput("WRONG_USERNAME", "wrongPassword"))

		assertEquals(HttpStatus.NOT_FOUND, login.statusCode)
	}

}