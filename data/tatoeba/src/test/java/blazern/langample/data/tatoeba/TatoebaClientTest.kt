package blazern.langample.data.tatoeba

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals

class TatoebaClientTest {
    private lateinit var response: Either<IOException, String>

    private val mockEngine = MockEngine {
        response.fold(
            { throw it },
            {
                respond(
                    content = ByteReadChannel(it),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )

    }

    private val kotlinClientHolder = KtorClientHolder(mockEngine)
    private val tatoeba = TatoebaClient(kotlinClientHolder)

    @Test
    fun `search returns only langTo translations`() = runBlocking {
        setResponse("""
            {
              "results": [
                {
                  "text": "Hello!",
                  "lang": "eng",
                  "translations": [
                    [
                      { "text": "Hallo!", "lang": "deu" },
                      { "text": "Grüß Gott!", "lang": "deu" },
                      { "text": "Salut!", "lang": "rus" }
                    ]
                  ]
                },
                {
                  "text": "Hello there",
                  "lang": "eng",
                  "translations": [
                    [
                      { "text": "Guten Tag", "lang": "deu" }
                    ]
                  ]
                },
                {
                  "text": "Good morning.",
                  "lang": "eng",
                  "translations": [
                    [
                      { "text": "Bonjour.", "lang": "rus" }
                    ]
                  ]
                }
              ]
            }
        """.trimIndent())

        val result = tatoeba
            .search("hello", Lang.EN, Lang.DE, page = 0)
            .getOrElse { throw it.e!! }

        val expected = listOf(
            TranslationsSet(
                original = Sentence("Hello!", Lang.EN, DataSource.TATOEBA),
                translations = listOf(
                    Sentence("Hallo!", Lang.DE, DataSource.TATOEBA),
                    Sentence("Grüß Gott!", Lang.DE, DataSource.TATOEBA),
                ),
                translationsQualities = listOf(QUALITY_MAX, QUALITY_MAX),
            ),
            TranslationsSet(
                original = Sentence("Hello there", Lang.EN, DataSource.TATOEBA),
                translations = listOf(
                    Sentence("Guten Tag", Lang.DE, DataSource.TATOEBA),
                ),
                translationsQualities = listOf(QUALITY_MAX),
            ),
        )

        assertEquals(expected, result)
    }

    @Test
    fun `empty results`() = runBlocking {
        setResponse("""{ "results": [] }""")

        val result = tatoeba
            .search("hello", Lang.EN, Lang.DE, page = 0)
            .getOrElse { throw it.e!! }
        val expected = emptyList<TranslationsSet>()
        assertEquals(expected, result)
    }

    @Test
    fun exception() = runBlocking {
        val e = IOException("no internet")
        setResponse(e)

        val result = tatoeba.search("hello", Lang.EN, Lang.DE, page = 0)
        val expected = Either.Left(e)
        assertEquals(expected.value, result.leftOrNull()!!.e!!.cause)
    }

    private fun setResponse(json: String) {
        response = Either.Right(json)
    }

    private fun setResponse(exception: IOException) {
        response = Either.Left(exception)
    }
}
