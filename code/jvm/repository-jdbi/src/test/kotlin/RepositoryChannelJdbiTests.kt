package pt.isel

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositoryChannelJdbiTests : RepositoryJdbiTests() {

    private val channelName = "Channel"
    private val channelDescription = "Description of Channel"
    private val channelVisibility = Visibility.PRIVATE
    private var channelAdminUserId : UInt = 0u


    @BeforeEach
    fun addAdminUser() {
        runWithHandle { handle: Handle ->
            val user = RepositoryUserJdbi(handle).createUser("Alice",Email("alice99@email.com"),UUID.randomUUID(),"password")
            channelAdminUserId = user.id
        }
    }

    @Test
    fun `test create channel and find it`() =
        runWithHandle { handle ->
            val repoChannel = RepositoryChannelJdbi(handle)
            repoChannel.createChannel(
                channelName,
                channelDescription,
                channelAdminUserId,
                channelVisibility
            )
            val channels = repoChannel.findAll()
            assertEquals(1, channels.size)
        }

    @Test
    fun `test create multiple channels and find them`() =
        runWithHandle { handle ->
            val numberOfChannelsCreated = 5
            val repoChannels = RepositoryChannelJdbi(handle)
            repeat(numberOfChannelsCreated){
                repoChannels.createChannel(
                    channelName + it.toString(),
                    channelDescription,
                    channelAdminUserId,
                    channelVisibility
                )
            }
            val channels = repoChannels.findAll()
            assertEquals(numberOfChannelsCreated, channels.size)
            repeat(numberOfChannelsCreated) { idx ->
                assertTrue(channels.any{ it.name == channelName + idx.toString()})
            }
        }

    @Test
    fun `test create channel and find it by id`() =
        runWithHandle { handle ->
            val repoChannel = RepositoryChannelJdbi(handle)
            val channelCreated = repoChannel.createChannel(
                channelName,
                channelDescription,
                channelAdminUserId,
                channelVisibility
            )
            val channel = repoChannel.findById(channelCreated.id)
            assertNotNull(channel)
            assertEquals(channelCreated, channel)
        }

    @Test
    fun `test modify channel and save it`() =
        runWithHandle { handle ->
            val repoChannels = RepositoryChannelJdbi(handle)
            val channel =
                repoChannels.createChannel(
                    channelName,
                    channelDescription,
                    channelAdminUserId,
                    channelVisibility
                )
            val modifiedChannel = channel.copy(description = channelDescription + "modified")
            repoChannels.save(modifiedChannel)
            val channelFound = repoChannels.findById(channel.id)
            assertNotNull(channelFound)
            assertEquals(modifiedChannel, channelFound)
        }

    @Test
    fun `test delete channel`() =
        runWithHandle { handle ->
            val repoChannels = RepositoryChannelJdbi(handle)
            val channelCreated =
                repoChannels.createChannel(
                    channelName,
                    channelDescription,
                    channelAdminUserId,
                    channelVisibility
                )
            val channels = repoChannels.findAll()
            assertEquals(1, channels.size)
            repoChannels.deleteById(channelCreated.id)
            val channelsAfterDelete = repoChannels.findAll()
            assertEquals(0, channelsAfterDelete.size)
        }

    @Test
    fun `test clear channels`() =
        runWithHandle { handle ->
            val numOfChannelsCreated = 5
            val repoChannels = RepositoryChannelJdbi(handle)
            repeat(numOfChannelsCreated){
                repoChannels.createChannel(
                    channelName + it.toString(),
                    channelDescription,
                    channelAdminUserId,
                    channelVisibility
                )
            }
            val channels = repoChannels.findAll()
            assertEquals(numOfChannelsCreated, channels.size)
            repoChannels.clear()
            val channelsAfterClear = repoChannels.findAll()
            assertEquals(0, channelsAfterClear.size)
        }

    @Test
    fun `test create channel and find it by name`() =
        runWithHandle { handle ->
            val repoChannels = RepositoryChannelJdbi(handle)
            val channelCreated =
                repoChannels.createChannel(
                    channelName,
                    channelDescription,
                    channelAdminUserId,
                    channelVisibility
                )
            val channel = repoChannels.findByName(channelName)
            assertNotNull(channel)
            assertEquals(channel, channelCreated)
        }

    @Test
    fun `test find all public channels`() =
        runWithHandle { handle ->
            val numOfPublicChannelsCreated = 5
            val numOfPrivateChannelsCreated = 3
            val repoChannels = RepositoryChannelJdbi(handle)
            repeat(numOfPrivateChannelsCreated){
                repoChannels.createChannel(
                    channelName + it.toString() + "Private",
                    channelDescription,
                    channelAdminUserId,
                    Visibility.PRIVATE
                )
            }
            repeat(numOfPublicChannelsCreated){
                repoChannels.createChannel(
                    channelName + it.toString() + "Public",
                    channelDescription,
                    channelAdminUserId,
                    Visibility.PUBLIC
                )
            }
            val totalChannels = repoChannels.findAll()
            assertEquals(numOfPrivateChannelsCreated + numOfPublicChannelsCreated, totalChannels.size)
            val publicChannels = repoChannels.getPublicChannels()
            assertEquals( numOfPublicChannelsCreated, publicChannels.size)
            repeat(numOfPublicChannelsCreated) {idx ->
                assertTrue(publicChannels.any{ it.name == (channelName + idx.toString() + "Public")})
            }
        }

}