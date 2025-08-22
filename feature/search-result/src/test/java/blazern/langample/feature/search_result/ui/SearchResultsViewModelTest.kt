package blazern.langample.feature.search_result.ui

import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import blazern.langample.domain.model.priority
import blazern.langample.domain.model.toClass
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.detailsOfType
import blazern.langample.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `received lexical items details sorting`() = runTest(mainDispatcherRule.testDispatcher) {
        val detailsMultiplier = 2
        val sources = fullSourcesWithFullDetails(detailsMultiplier = detailsMultiplier)
        val viewModel = SearchResultsViewModel(
            startQuery = "query",
            langFrom = Lang.EN,
            langTo = Lang.DE,
            dataSources = sources,
        )

        while (true) {
            val loadings = LexicalItemDetail.Type.entries
                .map { it.toClass() }
                .map { viewModel.state.value.detailsOfType(it) }
                .flatten()
                .filterIsInstance<LexicalItemDetailState.Loading<*>>()
            if (loadings.isEmpty()) {
                break
            }
            loadings.forEach { viewModel.onLoadingDetailVisible(it) }
            advanceUntilIdle()
        }

        val sortedDataSources = List(detailsMultiplier) { DataSource.entries }
            .flatten()
            .sortedBy { it.priority }
        LexicalItemDetail.Type.entries.forEach {
            val details = viewModel.state.value.detailsOfType(it.toClass())
            assertEquals(
                sortedDataSources,
                details.map { it.source },
                details.toString(),
            )
        }
    }

    private fun fullSourcesWithFullDetails(
        detailsMultiplier: Int = 1,
    ): List<FakeLexicalItemDetailsSource> {
        // Every type of DataSource
        val sources = DataSource.entries.map { source ->
            var details = mutableListOf<LexicalItemDetail>()
            details += List(detailsMultiplier) { LexicalItemDetail.Forms("forms $detailsMultiplier $it", source) }
            details += List(detailsMultiplier) { LexicalItemDetail.Explanation("Wörter $detailsMultiplier $it", source)}
            details += List(detailsMultiplier) {
                LexicalItemDetail.WordTranslations(
                    TranslationsSet(
                        original = Sentence("text $detailsMultiplier $it", Lang.EN, source),
                        translations = listOf(Sentence("Text", Lang.DE, source)),
                        translationsQualities = listOf(QUALITY_MAX),
                    ),
                    source,
                )
            }
            details += List(detailsMultiplier) {
                LexicalItemDetail.Synonyms(
                    TranslationsSet(
                        original = Sentence("text $detailsMultiplier $it", Lang.EN, source),
                        translations = listOf(Sentence("string", Lang.EN, source)),
                        translationsQualities = listOf(QUALITY_MAX),
                    ),
                    source,
                )
            }
            details += List(detailsMultiplier) {
                LexicalItemDetail.Example(
                    TranslationsSet(
                        original = Sentence("nice text $detailsMultiplier $it", Lang.EN, source),
                        translations = listOf(Sentence("schöner Text", Lang.DE, source)),
                        translationsQualities = listOf(QUALITY_MAX),
                    ),
                    source,
                )
            }
            // Make sure we didn't forget a type
            LexicalItemDetail.Type.entries.forEach { type ->
                assertTrue(details.any { it.type == type })
            }
            FakeLexicalItemDetailsSource(
                source = source,
                details = details,
            )
        }
        return sources
    }
}

private class FakeLexicalItemDetailsSource(
    override val source: DataSource,
    val details: List<LexicalItemDetail>,
) : LexicalItemDetailsSource {
    override val types = LexicalItemDetail.Type.entries.toList()

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow = flow {
        details.forEach { emit(Right(it)) }
    }
}
