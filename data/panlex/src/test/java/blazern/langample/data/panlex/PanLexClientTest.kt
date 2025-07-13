package blazern.langample.data.panlex

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.panlex.model.WordData
import blazern.langample.domain.model.Lang
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.ContentConvertException
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Test

import org.junit.Assert.*

class PanLexClientTest {

    private var responses = ArrayDeque<Either<Exception, String>>()

    private val mockEngine = MockEngine {
        val current = responses.removeFirstOrNull() ?: error("No mock response configured")
        current.fold(
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

    private val clientHolder = KtorClientHolder(mockEngine)
    private val panlex = PanLexClient(clientHolder)

    @Test
    fun `search returns WordData with translations and synonyms`() = runBlocking {
        // NOTE: it's very bad that we're relying on a certain order of requests here
        setResponses(
            LANGVAR_JSON,
            TRANSLATION_IDS_JSON,
            TRANSLATIONS_JSON,
            DENOTATION_INIT_JSON,
            DENOTATION_MEANINGS_JSON,
            SYNONYMS_JSON,
        )

        val result = panlex.search(
            query = "Haus",
            langFrom = Lang.DE,
            langsTo = listOf(Lang.EN, Lang.RU)
        ).getOrElse { throw it }

        val expected = WordData(
            word = "Haus",
            lang = Lang.DE,
            translations = listOf(
                WordData("house", Lang.EN),
                WordData("дом", Lang.RU),
            ),
            synonyms = listOf(
                WordData("Haus", Lang.DE),
                WordData("Gebäude", Lang.DE),
                WordData("Bauwerk", Lang.DE),
            )
        )

        assertEquals(expected, result)
    }

    @Test
    fun `network exception is wrapped in Either Left`() = runBlocking {
        val io = IOException("no internet")
        setResponse(io)

        val result = panlex.search("Haus", Lang.DE, listOf(Lang.EN))
        assertEquals(io, result.leftOrNull()?.cause)
    }

    @Test
    fun `serialization problems are wrapped in Either Left`() = runBlocking {
        setResponses("{ not valid json }")

        val result = panlex.search("Haus", Lang.DE, listOf(Lang.EN))
        val error = result.leftOrNull()
        assertTrue(error is ContentConvertException)
    }

    private fun setResponses(vararg bodies: String) {
        responses = ArrayDeque(bodies.map { Either.Right(it) })
    }

    private fun setResponse(exception: Exception) {
        responses = ArrayDeque(listOf(Either.Left(exception)))
    }

    companion object {
        private const val LANGVAR_JSON = """
            {
              "resultType":"langvar",
              "result":[
                {"id":1,"lang_code":"deu","uid":"deu-000"},
                {"id":2,"lang_code":"eng","uid":"eng-000"},
                {"id":3,"lang_code":"rus","uid":"rus-000"}
              ],
              "resultNum":3,
              "resultMax":2000
            }
        """

        private const val TRANSLATION_IDS_JSON = """
            {
              "resultType":"expr",
              "result":[
                {"id":100,"langvar":1,"txt":"Haus","trans_expr":200},
                {"id":100,"langvar":1,"txt":"Haus","trans_expr":201}
              ],
              "resultNum":2,
              "resultMax":2000
            }
        """

        private const val TRANSLATIONS_JSON = """
            {
              "resultType":"expr",
              "result":[
                {"id":200,"langvar":2,"txt":"house"},
                {"id":201,"langvar":3,"txt":"дом"}
              ],
              "resultNum":2,
              "resultMax":2000
            }
        """

        private const val DENOTATION_INIT_JSON = """
            {
              "resultType":"denotation",
              "result":[
                {"id":400,"expr":100,"meaning":300,"source":1}
              ],
              "resultNum":1,
              "resultMax":2000
            }
        """

        private const val DENOTATION_MEANINGS_JSON = """
            {
              "resultType":"denotation",
              "result":[
                {"id":401,"expr":100,"meaning":300,"source":1},
                {"id":402,"expr":110,"meaning":300,"source":1},
                {"id":403,"expr":111,"meaning":300,"source":1}
              ],
              "resultNum":3,
              "resultMax":2000
            }
        """

        private const val SYNONYMS_JSON = """
            {
              "resultType":"expr",
              "result":[
                {"id":100,"langvar":1,"txt":"Haus"},
                {"id":110,"langvar":1,"txt":"Gebäude"},
                {"id":111,"langvar":1,"txt":"Bauwerk"}
              ],
              "resultNum":3,
              "resultMax":2000
            }
        """
    }
}