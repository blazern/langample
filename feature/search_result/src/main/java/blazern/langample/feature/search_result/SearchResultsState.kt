package blazern.langample.feature.search_result

import androidx.compose.runtime.Immutable
import blazern.langample.data.tatoeba.model.SentencesPair

@Immutable
sealed class SearchResultsState {
    data object PerformingSearch : SearchResultsState()
    data object Error : SearchResultsState()

    @Immutable
    data class Results(
        val explanation: String,
        val examples: List<SentencesPair>,
    ) : SearchResultsState()
}
