package blazern.langample.data.lexical_item_details_source.panlex

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.panlex.PanLexClient
import blazern.langample.data.panlex.model.WordData
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.Test

import org.junit.Assert.*

class PanLexLexicalItemDetailsSourceTest {
    private val panLexClient = mockk<PanLexClient>()
    private val source: LexicalItemDetailsSource = PanLexLexicalItemDetailsSource(panLexClient)

    private val wordData = WordData(
        word = "Haus",
        lang = Lang.DE,
        translations = listOf(
            WordData("house", Lang.EN),
        ),
        synonyms = listOf(
            WordData("Gebäude", Lang.DE),
            WordData("Bauwerk", Lang.DE),
        )
    )

    @Test
    fun `source id and supported types`() = runBlocking {
        assertEquals(DataSource.PANLEX, source.source)
        assertEquals(
            listOf(
                LexicalItemDetail.Type.WORD_TRANSLATIONS,
                LexicalItemDetail.Type.SYNONYMS,
            ),
            source.types
        )
    }

    @Test
    fun `emits translations and synonyms correctly`() = runBlocking {
        coEvery {
            panLexClient.search(any(), any(), any())
        } returns Either.Right(wordData)

        val results = source.request("Haus", Lang.DE, Lang.EN)
            .toList()
            .map { it.getOrElse { throw it } }

        val expected = listOf(
            LexicalItemDetail.WordTranslations(
                TranslationsSet(
                    original = Sentence("Haus", Lang.DE, DataSource.PANLEX),
                    translations = listOf(
                        Sentence("house", Lang.EN, DataSource.PANLEX)
                    )
                ),
                DataSource.PANLEX
            ),
            LexicalItemDetail.Synonyms(
                TranslationsSet(
                    original = Sentence("Haus", Lang.DE, DataSource.PANLEX),
                    translations = listOf(
                        Sentence("Gebäude", Lang.DE, DataSource.PANLEX),
                        Sentence("Bauwerk", Lang.DE, DataSource.PANLEX),
                    )
                ),
                DataSource.PANLEX
            )
        )

        assertEquals(expected, results)
    }

    @Test
    fun `recovers after initial error`() = runTest {
        val io = IOException("no internet")
        coEvery {
            panLexClient.search(any(), any(), any())
        } returns Either.Left(io)

        val flow = source.request("Haus", Lang.DE, Lang.EN)
        val iter = FlowIterator(flow, this)

        assertTrue(iter.next() is Either.Left)

        coEvery {
            panLexClient.search(any(), any(), any())
        } returns Either.Right(wordData)

        assertTrue(iter.next() is Either.Right)
        iter.close()
    }
}
