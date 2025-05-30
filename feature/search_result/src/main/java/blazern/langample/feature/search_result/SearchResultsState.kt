package blazern.langample.feature.search_result

import androidx.compose.runtime.Immutable

@Immutable
sealed class SearchResultsState {
    data object PerformingSearch : SearchResultsState()
    data object Error : SearchResultsState()

    @Immutable
    data class Results(
        val explanation: String,
        val examples: List<Any>,
    ) : SearchResultsState()
}
