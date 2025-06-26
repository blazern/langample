package blazern.langample.feature.search_result

import androidx.compose.runtime.Immutable
import blazern.langample.domain.model.TranslationsSet

@Immutable
sealed class SearchResultsState {
    data object PerformingSearch : SearchResultsState()
    data object Error : SearchResultsState()

    @Immutable
    data class Results(
        val formsHtml: String,
        val explanation: String,
        val examples: List<TranslationsSet>,
    ) : SearchResultsState()
}
