package pt.isel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.postgresql.ds.PGSimpleDataSource
import kotlin.math.abs
import kotlin.random.Random


open class RepositoryJdbiTests {
    companion object {
        @JvmStatic
        protected fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        @JvmStatic
        protected fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

        protected val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()
    }

    @BeforeEach
    fun clean() {
        runWithHandle { handle: Handle ->
            RepositoryMessageJdbi(handle).clear()
            RepositoryParticipantJdbi(handle).clear()
            RepositoryChannelInvitationJdbi(handle).clear()
            RepositoryRegisterInvitationJdbi(handle).clear()
            RepositoryChannelJdbi(handle).clear()
            RepositoryUserJdbi(handle).clear()
        }
    }

}
