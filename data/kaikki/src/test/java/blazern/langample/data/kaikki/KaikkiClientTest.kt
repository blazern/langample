package blazern.langample.data.kaikki

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.domain.model.Lang
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import org.junit.Test

import org.junit.Assert.*

class KaikkiClientTest {
    private lateinit var response: Either<Exception, String>

    private val mockEngine = MockEngine {
        response.fold(
            { throw it },
            // Simulate successful HTTP 200 with the supplied body
            {
                respond(
                    content = ByteReadChannel(it),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/octet-stream")
                )
            }
        )
    }

    private val clientHolder = KtorClientHolder(mockEngine)
    private val kaikki = KaikkiClient(clientHolder)

    @Test
    fun `search returns list of entries`() = runBlocking {
        setResponse(
            """
            {"word":"Haus","pos":"noun","pos_title":"Substantiv","lang_code":"de","lang":"German"}
            {"word":"Haus","pos":"verb","pos_title":"Verb","lang_code":"de","lang":"German"}
            """.trimIndent()
        )

        val result = kaikki.search("Haus", Lang.DE).getOrElse { throw it }

        val expected = listOf(
            Entry(
                word = "Haus",
                pos = "noun",
                posTitle = "Substantiv",
                langCode = "de",
                lang = "German"
            ),
            Entry(
                word = "Haus",
                pos = "verb",
                posTitle = "Verb",
                langCode = "de",
                lang = "German"
            )
        )

        assertEquals(expected, result)
    }

    @Test
    fun `empty body yields empty list`() = runBlocking {
        setResponse("")
        val result = kaikki.search("Haus", Lang.DE).getOrElse { throw it }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `network exception is wrapped in Either Left`() = runBlocking {
        val io = IOException("no internet")
        setResponse(io)
        val result = kaikki.search("Haus", Lang.DE)
        assertEquals(io, result.leftOrNull()?.cause)
    }

    @Test
    fun `serialization problems are wrapped in Either Left`() = runBlocking {
        setResponse("{ this is not valid json }")
        val result = kaikki.search("Haus", Lang.DE)
        val error = result.leftOrNull()
        assertTrue(error is SerializationException)
    }

    private fun setResponse(body: String) {
        response = Either.Right(body)
    }

    private fun setResponse(exception: Exception) {
        response = Either.Left(exception)
    }
}
