package blazern.langample.feature.search_result

import androidx.compose.runtime.Immutable
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.TranslationsSet

@Immutable
internal sealed class SearchResultsState {
    data object PerformingSearch : SearchResultsState()
    data object Error : SearchResultsState()

    @Immutable
    data class Results(
        val formsHtml: String,
        val formsSource: DataSource,
        val explanation: String,
        val explanationSource: DataSource,
        val examples: List<TranslationsSet>,
    ) : SearchResultsState()
}
