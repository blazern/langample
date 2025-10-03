package blazern.langample.data.lexical_item_details_source.aggregation

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource.Item
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsAccentsEnhancer
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsAccentsEnhancerProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LexicalItemDetailsSourceAggregatorForQueryTest {

    private val enhancement = "enhancement"
    private val query = "tanz"
    private val langFrom = Lang.DE
    private val langTo = Lang.EN

    private val enhancer = mockk<FormsAccentsEnhancer> {
        every { enhance(any()) } answers {
            (arg<Sentence>(0)).copy(text = enhancement + firstArg<Sentence>().text)
        }
    }

    private val enhancerProvider = mockk<FormsAccentsEnhancerProvider> {
        coEvery { provideFor(any(), any(), any()) } returns Right(enhancer)
    }

    private fun createAggregator(
        sources: List<LexicalItemDetailsSource>,
    ) = LexicalItemDetailsSourceAggregatorForQuery(
        query = query,
        langFrom = langFrom,
        langTo = langTo,
        accentsEnhancerProvider = enhancerProvider,
        dataSources = sources,
    )

    private class FakeSource(
        override val source: DataSource,
        override val types: Set<LexicalItemDetail.Type> = LexicalItemDetail.Type.entries.toSet(),
        private val flow: Flow<Item>,
    ) : LexicalItemDetailsSource {
        override fun request(query: String, langFrom: Lang, langTo: Lang): Flow<Item> = flow
    }

    private fun example(detailSource: DataSource, text: String = "original"): Example =
        Example(
            translationsSet = TranslationsSet(
                original = Sentence(text, Lang.EN, detailSource),
                translations = listOf(Sentence("Beispiel", Lang.DE, detailSource)),
                translationsQualities = listOf(QUALITY_MAX)
            ),
            source = detailSource
        )

    @Test
    fun `request enhances only examples`() = runBlocking {
        val src = DataSource.TATOEBA
        val page = Item.Page(
            details = listOf(
                example(src, "ex1"),
                example(src, "ex2"),
                example(src, "ex3"),
            ),
            nextPageTypes = LexicalItemDetail.Type.entries.toSet()
        )

        val source = FakeSource(src, flow = flowOf(page))
        val aggregator = createAggregator(listOf(source))

        val emitted = aggregator.request(src).toList()

        val receivedPage = emitted.single() as Item.Page
        val receivedDetails = receivedPage.details

        val receivedExamples = receivedDetails.filterIsInstance<Example>()
        assertEquals(
            listOf(
                example(src, "${enhancement}ex1"),
                example(src, "${enhancement}ex2"),
                example(src, "${enhancement}ex3"),
            ),
            receivedExamples,
        )
    }

    @Test
    fun `enhancer is resolved only once`() = runBlocking {
        val source1 = DataSource.TATOEBA
        val source2 = DataSource.KAIKKI

        val types = LexicalItemDetail.Type.entries.toSet()
        val page1 = Item.Page(details = listOf(example(source1, "a1")), nextPageTypes = types)
        val page2 = Item.Page(details = listOf(example(source1, "a2")), nextPageTypes = types)
        val page3 = Item.Page(details = listOf(example(source2, "b1")), nextPageTypes = types)

        val src1 = FakeSource(source1, flow = flowOf(page1, page2))
        val src2 = FakeSource(source2, flow = flowOf(page3))

        val aggregator = createAggregator(listOf(src1, src2))

        var results = aggregator.request(source1).toList()
        results += aggregator.request(source2).toList()

        coVerify(exactly = 1) { enhancerProvider.provideFor(any(), any(), any()) }

        results.filterIsInstance<Item.Page>()
            .flatMap { it.details }
            .filterIsInstance<Example>()
            .forEach { ex ->
                assertTrue(ex.translationsSet.original.text.contains(enhancement))
            }
    }

    @Test
    fun `when enhancer provider fails, nothing is enhanced`() = runBlocking {
        val src = DataSource.CHATGPT
        val page = Item.Page(
            details = listOf(example(src, "no-change")),
            nextPageTypes = LexicalItemDetail.Type.entries.toSet()
        )
        val source = FakeSource(src, flow = flowOf(page))

        coEvery { enhancerProvider.provideFor(any(), any(), any()) } returns Left(Err.Other(null))
        val aggregator = createAggregator(listOf(source))
        val items = aggregator.request(src).toList()

        val emittedPage = items.single() as Item.Page
        val emittedExample = emittedPage.details.filterIsInstance<Example>().single()
        assertEquals("no-change", emittedExample.translationsSet.original.text)

        // Provider attempted exactly once, even though it has failed at the first attempt
        aggregator.request(src)
        aggregator.request(src)
        coVerify(exactly = 1) { enhancerProvider.provideFor(any(), any(), any()) }
    }
}