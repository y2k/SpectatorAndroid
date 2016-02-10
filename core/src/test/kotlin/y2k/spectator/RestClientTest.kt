package y2k.spectator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import y2k.spectator.service.RestClient
import java.util.*

/**
 * Created by y2k on 1/4/16.
 */
class RestClientTest {

//    @Ignore
//    @Test
//    fun testGetApi() {
//        val restClient = RestClient(MockCookieStorage())
//        var data: ByteArray? = null
//
//        restClient.api
//            .images(1697, 100, 100)
//            .map { it.bytes() }
//            .subscribe { data = it }
//
//        assertNotNull(data)
//        assertEquals(6393, data!!.size)
//    }
//
//    class MockCookieStorage : RestClient.CookieStorage {
//
//        override fun put(cookies: HashSet<String>) {
//        }
//
//        override fun getAll(): Set<String> {
//            return emptySet()
//        }
//    }
}