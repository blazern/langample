package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TatoebaLexicalItemDetailsSourceTest {
    private val tatoeba = mockk<TatoebaClient>()
    private val source = TatoebaLexicalItemDetailsSource(
        tatoeba,
        LexicalItemDetailsSourceCacher.NOOP,
    )

    private val translationsSets = listOf(
        TranslationsSet(
            original = Sentence("Hello", Lang.EN, DataSource.TATOEBA),
            translations = listOf(Sentence("Hallo", Lang.DE, DataSource.TATOEBA)),
            translationsQualities = listOf(QUALITY_MAX),
        ),
        TranslationsSet(
            original = Sentence("Good morning", Lang.EN, DataSource.TATOEBA),
            translations = listOf(Sentence("Guten Morgen", Lang.DE, DataSource.TATOEBA)),
            translationsQualities = listOf(QUALITY_MAX),
        ),
    )

    @Test
    fun `source and types`() = runBlocking {
        assertEquals(DataSource.TATOEBA, source.source)
        assertEquals(listOf(LexicalItemDetail.Type.EXAMPLE), source.types)
    }

    @Test
    fun `good scenario`() = runBlocking {
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE) } returns Right(translationsSets)

        val results = source.request("hello", Lang.EN, Lang.DE)
            .toList()
            .map { it.getOrNull()!! }

        val expected = translationsSets.map {
            LexicalItemDetail.Example(
                it,
                source = DataSource.TATOEBA,
            )
        }
        assertEquals(expected, results)
    }

    @Test
    fun `bad and then good scenario`() = runTest {
        // Bad
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE) } returns Left(IOException())
        val flow = source.request("hello", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow)
        assertTrue { iter.next() is Left }

        // Good
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE) } returns Right(translationsSets)
        assertTrue { iter.next() is Right }
        iter.close()
    }
}
