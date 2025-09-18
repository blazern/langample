package blazern.langample.model.lexical_item_details_source.utils.examples_tools

import arrow.core.getOrElse
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource.Item
import blazern.langample.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.WordForm
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormsForExamplesProviderTest {
    private val types = listOf(LexicalItemDetail.Type.FORMS)
    private val kaikki = mockk<KaikkiLexicalItemDetailsSource>()
    private val formsProvider = FormsForExamplesProvider(kaikki)

    @Test
    fun `uses and modifies Kaikki forms`() = runBlocking {
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
        coEvery { kaikki.request(any(), any(), any()) } returns flowOf(
            Item.Page(details = listOf(formsDetail), types)
        )

        val expectedForms = listOf(
            WordForm(
                text = "lache",
                tags = emptyList(),
                lang = Lang.DE
            ),
            WordForm(
                text = "lachst",
                tags = emptyList(),
                lang = Lang.DE,
            ),
            WordForm("Lachen", emptyList(), Lang.DE),
        )
        assertEquals(
            expectedForms,
            formsProvider.requestFor("lachen", Lang.DE, Lang.EN).getOrElse { throw it.e!! },
        )
    }

    @Test
    fun `iterats over flow until receives forms`() = runBlocking {
        val kaikkiForms = listOf(WordForm("lachen", emptyList(), Lang.DE))
        val formsDetail = Forms(Forms.Value.Detailed(kaikkiForms), DataSource.KAIKKI)
        coEvery { kaikki.request(any(), any(), any()) } returns flowOf(
            Item.Page(details = listOf(Explanation("Explanation", DataSource.KAIKKI)), types),
            Item.Page(details = listOf(LexicalItemDetail.Example(TranslationsSet(
                Sentence("", Lang.DE, DataSource.KAIKKI), emptyList(), emptyList(),
            ), DataSource.KAIKKI)), types),
            Item.Page(details = listOf(formsDetail), types),
        )

        assertEquals(
            kaikkiForms,
            formsProvider.requestFor("lachen", Lang.DE, Lang.EN).getOrElse { throw it.e!! },
        )
    }

    @Test
    fun `first error stops requests`() = runBlocking {
        val kaikkiForms = listOf(WordForm("lachen", emptyList(), Lang.DE))
        val formsDetail = Forms(Forms.Value.Detailed(kaikkiForms), DataSource.KAIKKI)
        coEvery { kaikki.request(any(), any(), any()) } returns flowOf(
            Item.Page(details = listOf(Explanation("Explanation", DataSource.KAIKKI)), types),
            Item.Failure(Err.from(Exception())),
            Item.Page(details = listOf(formsDetail), types),
        )

        val result = formsProvider.requestFor("lachen", Lang.DE, Lang.EN)
        assertTrue(result.isLeft())
    }
}
