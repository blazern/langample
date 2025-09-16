package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import blazern.langample.domain.model.WordForm
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TatoebaLexicalItemDetailsSourceTest {
    private val tatoeba = mockk<TatoebaClient>()
    private val kaikki = mockk<KaikkiLexicalItemDetailsSource>(relaxed = true)
    private val source = TatoebaLexicalItemDetailsSource(
        tatoeba,
        LexicalItemDetailsSourceCacher.NOOP,
        kaikki,
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
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 1) } returns Right(translationsSets)
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 2) } returns Right(emptyList())

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
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 1) } returns Left(IOException())
        val flow = source.request("hello", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow)
        assertTrue { iter.next() is Left }

        // Good
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 1) } returns Right(translationsSets)
        assertTrue { iter.next() is Right }
        iter.close()
    }


    @Test
    fun `uses Kaikki forms to refine Tatoeba query`() = runTest {
        val kaikkiForms = listOf(
            WordForm(
                text = "ich lache",
                tags = emptyList(),
                lang = Lang.DE
            ),
            WordForm(
                text = "du lachst",
                tags = emptyList(),
                lang = Lang.DE,
            ),
            WordForm("haben", listOf(WordForm.Tag.Defined.Auxiliary("aux")), Lang.DE),
            WordForm("der Lachen", emptyList(), Lang.DE),
        )
        val formsDetail = Forms(Forms.Value.Detailed(kaikkiForms), DataSource.KAIKKI)
        coEvery { kaikki.request(any(), any(), any()) } returns flowOf(Right(formsDetail))

        coEvery { tatoeba.search(any(), any(), any(), 1) } returns Right(emptyList())
        source.request("lachen", Lang.DE, Lang.EN).toList().map { it.getOrNull()!! }

        val querySlot = slot<String>()
        coVerify { tatoeba.search(capture(querySlot), any(), any(), 1) }
        assertEquals("(=lache|=lachst|=Lachen)", querySlot.captured)
    }

    @Test
    fun `paginates and retries same page after error`() = runTest {
         val page1 = listOf(
            TranslationsSet(
                original = Sentence("Hello", Lang.EN, DataSource.TATOEBA),
                translations = listOf(Sentence("Hallo", Lang.DE, DataSource.TATOEBA)),
                translationsQualities = listOf(QUALITY_MAX),
            )
        )
        val page2 = listOf(
            TranslationsSet(
                original = Sentence("Hi", Lang.EN, DataSource.TATOEBA),
                translations = listOf(Sentence("Hi!", Lang.DE, DataSource.TATOEBA)),
                translationsQualities = listOf(QUALITY_MAX),
            )
        )

        // Page 1 OK, Page 2 fails once then succeeds, Page 3 empty => stop
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 1) } returns Right(page1)
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 2) } returnsMany listOf(
            Left(IOException("transient")),
            Right(page2)
        )
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE, 3) } returns Right(emptyList())

        val flow = source.request("hello", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow)

        // Page 1 emits its examples
        val first = iter.next()
        assertEquals(
            LexicalItemDetail.Example(page1[0], DataSource.TATOEBA),
            (first as Right).value
        )

        // First attempt of Page 2 emits an error
        assertTrue(iter.next() is Left)

        // Retry of Page 2 succeeds and emits its examples
        val third = iter.next()
        assertEquals(
            LexicalItemDetail.Example(page2[0], DataSource.TATOEBA),
            (third as Right).value
        )

        iter.close()
    }
}
