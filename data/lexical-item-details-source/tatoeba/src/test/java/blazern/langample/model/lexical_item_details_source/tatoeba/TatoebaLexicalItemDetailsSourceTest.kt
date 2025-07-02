package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TatoebaLexicalItemDetailsSourceTest {
    private val tatoeba = mockk<TatoebaClient>()
    private val source = TatoebaLexicalItemDetailsSource(tatoeba)

    @Test
    fun `good scenario`() = runBlocking {
        val translationsSets = listOf(
            TranslationsSet(
                original = Sentence("Hello", Lang.EN, DataSource.TATOEBA),
                translations = listOf(Sentence("Hallo", Lang.DE, DataSource.TATOEBA))
            ),
            TranslationsSet(
                original = Sentence("Good morning", Lang.EN, DataSource.TATOEBA),
                translations = listOf(Sentence("Guten Morgen", Lang.DE, DataSource.TATOEBA))
            ),
        )
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE) } returns Right(translationsSets)

        val futureResults = source.request("hello", Lang.EN, Lang.DE).single()

        assertEquals(
            LexicalItemDetail.Type.EXAMPLE,
            futureResults.type,
        )
        assertEquals(
            DataSource.TATOEBA,
            futureResults.source,
        )

        val results = futureResults.details.toList()
            .map { it.getOrElse { throw it } }
        val expected = translationsSets.map {
            LexicalItemDetail.Example(
                it,
                source = DataSource.TATOEBA,
            )
        }
        assertEquals(expected, results)
    }

    @Test
    fun `bad scenario`() = runBlocking {
        coEvery { tatoeba.search("hello", Lang.EN, Lang.DE) } returns Left(IOException())
        val futureResults = source.request("hello", Lang.EN, Lang.DE).single()
        val results = futureResults.details.toList()
        assertEquals(1, results.size)
        assertTrue { results.first() is Left }
    }
}
