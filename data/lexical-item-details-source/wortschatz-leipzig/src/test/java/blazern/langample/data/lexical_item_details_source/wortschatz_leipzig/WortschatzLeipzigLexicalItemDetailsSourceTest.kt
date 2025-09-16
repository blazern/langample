package blazern.langample.data.lexical_item_details_source.wortschatz_leipzig

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.utils.FlowIterator
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertIs

class WortschatzLeipzigLexicalItemDetailsSourceTest {
    private val responses = mutableListOf<Either<Exception, Pair<HttpStatusCode, String>>>()

    private val mockEngine = MockEngine { _ ->
        val next = responses.removeFirstOrNull()
            ?: Either.Right(HttpStatusCode.OK to """{"count":0,"sentences":[]}""")
        next.fold(
            { throw it },
            { (code, body) ->
                respond(
                    content = ByteReadChannel(body),
                    status = code,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )
    }

    private val clientHolder = KtorClientHolder(mockEngine)
    private val source: LexicalItemDetailsSource =
        WortschatzLeipzigLexicalItemDetailsSource(clientHolder)

    private fun enqueue(body: String, code: HttpStatusCode = HttpStatusCode.OK) {
        responses.add(Either.Right(code to body))
    }

    private fun enqueueError(e: Exception) {
        responses.add(Either.Left(e))
    }

    private fun exampleJson(vararg pairs: Pair<String, String>): String {
        val items = pairs.joinToString(",") { (id, sent) ->
            """{"id":"$id","sentence":"$sent"}"""
        }
        return """{"count":${pairs.size},"sentences":[$items]}"""
    }

    private fun example(text: String, lang: Lang) = LexicalItemDetail.Example(
        translationsSet = TranslationsSet(
            original = Sentence(text, lang, DataSource.WORTSCHATZ_LEIPZIG),
            translations = emptyList(),
            translationsQualities = emptyList(),
        ),
        source = DataSource.WORTSCHATZ_LEIPZIG
    )

    @Before
    fun setUp() {
        responses.clear()
    }

    @Test
    fun `source id and supported types`() = runTest {
        assertEquals(DataSource.WORTSCHATZ_LEIPZIG, source.source)
        assertEquals(listOf(LexicalItemDetail.Type.EXAMPLE), source.types)
    }

    @Test
    fun `emits examples from a single page and dedupes within page`() = runTest {
        enqueue(
            exampleJson(
                "42" to "Cat sits",
                "42" to "Cat sits 2",
                "43" to "Cat sleeps"
            )
        )

        val results = source.request("cat", Lang.EN, Lang.RU)
            .toList()
            .map { it.getOrElse { throw it } }

        val expected = listOf(
            example("Cat sits", Lang.EN),
            example("Cat sleeps", Lang.EN)
        )
        assertEquals(expected, results)
    }

    @Test
    fun `paginates until short page`() = runTest {
        // LIMIT = 10; first page has 10 items, second has 3 -> stop
        enqueue(exampleJson(*(0 until 10).map { "p1-$it" to "Satz $it" }.toTypedArray()))
        enqueue(exampleJson("p2-0" to "Satz 10", "p2-1" to "Satz 11", "p2-2" to "Satz 12"))

        val results = source.request("Haus", Lang.DE, Lang.EN)
            .toList()
            .map { it.getOrElse { throw it } }

        val expected = buildList {
            addAll((0 until 10).map { example("Satz $it", Lang.DE) })
            addAll(listOf(
                example("Satz 10", Lang.DE),
                example("Satz 11", Lang.DE),
                example("Satz 12", Lang.DE),
            ))
        }
        assertEquals(expected, results)
    }

    @Test
    fun `dedupes across pages`() = runTest {
        // First page has id=1..3; second page repeats id=3 and adds id=4
        enqueue(exampleJson("1" to "uno", "2" to "dos", "3" to "tres"))
        enqueue(exampleJson("3" to "tres (dup)", "4" to "cuatro"))

        val results = source.request("algo", Lang.EN, Lang.DE)
            .toList()
            .map { it.getOrElse { throw it } }

        val expected = listOf(
            example("uno", Lang.EN),
            example("dos", Lang.EN),
            example("tres", Lang.EN),
            example("cuatro", Lang.EN),
        )
        assertEquals(expected, results)
    }

    @Test
    fun `recovers after initial error`() = runTest {
        enqueueError(IOException("no internet"))
        enqueue(exampleJson("100" to "First successful example"))

        val flow = source.request("кот", Lang.EN, Lang.RU)
        val iter = FlowIterator(flow)

        assertIs<Either.Left<*>>(iter.next())
        val next = iter.next()

        assertIs<Either.Right<*>>(next)
        val right = next.value
        assertEquals(example("First successful example", Lang.EN), right)

        iter.close()
    }
}
