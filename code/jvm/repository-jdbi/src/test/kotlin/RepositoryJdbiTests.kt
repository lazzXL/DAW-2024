package pt.isel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.util.UUID
import kotlin.test.assertEquals


open class RepositoryJdbiTests {
    companion object {
        @JvmStatic
        protected fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

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
