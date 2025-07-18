package blazern.langample.data.lexical_item_details_source.kaikki

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.kaikki.KaikkiClient
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.data.kaikki.model.Example
import blazern.langample.data.kaikki.model.Form
import blazern.langample.data.kaikki.model.Sense
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.settings.SettingsRepository
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

class KaikkiLexicalItemDetailsSourceTest {
    private val kaikkiClient = mockk<KaikkiClient>()
    private val settings = mockk<SettingsRepository>()
    private val source: LexicalItemDetailsSource =
        KaikkiLexicalItemDetailsSource(kaikkiClient, settings)

    private val acceptableTags = listOf(setOf("plural"))
        .also { coEvery { settings.getTatoebaAcceptableTagsSets() } returns it }

    private val entry = Entry(
        word = "Haus",
        pos = "noun",
        posTitle = "Substantiv",
        langCode = "de",
        lang = "German",
        senses = listOf(
            Sense(
                glosses = listOf("house; building"),
                examples = listOf(
                    Example("Das ist ein Haus.")
                )
            )
        ),
        forms = listOf(
            Form("Häuser", tags = listOf("plural"))
        )
    )

    @Test
    fun `source id and supported types`() = runBlocking {
        assertEquals(DataSource.KAIKKI, source.source)
        assertEquals(
            listOf(
                LexicalItemDetail.Type.FORMS,
                LexicalItemDetail.Type.EXPLANATION,
                LexicalItemDetail.Type.EXAMPLE,
            ),
            source.types
        )
    }

    @Test
    fun `parses forms glosses and examples correctly`() = runBlocking {
        coEvery { kaikkiClient.search("Haus", Lang.DE) } returns Right(listOf(entry))

        val results = source.request("Haus", Lang.DE, Lang.EN)
            .toList()
            .map { it.getOrElse { throw it } }

        val expected = listOf(
            LexicalItemDetail.Forms("Häuser", DataSource.KAIKKI),
            LexicalItemDetail.Explanation("house; building", DataSource.KAIKKI),
            LexicalItemDetail.Example(
                TranslationsSet(
                    original = Sentence("Das ist ein Haus.", Lang.EN, DataSource.KAIKKI),
                    translations = emptyList(),
                ),
                DataSource.KAIKKI
            )
        )

        assertEquals(expected, results)
    }

    @Test
    fun `recovers after initial error`() = runTest {
        val io = IOException("no internet")
        coEvery { kaikkiClient.search("Haus", Lang.DE) } returns Left(io)

        val flow = source.request("Haus", Lang.DE, Lang.EN)
        val iter = FlowIterator(flow, this)

        assertTrue(iter.next() is Left)

        coEvery { kaikkiClient.search("Haus", Lang.DE) } returns Right(listOf(entry))

        assertTrue(iter.next() is Right)
        iter.close()
    }
}