package blazern.langample.feature.search_result

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SearchResultsViewModel : ViewModel() {
    private val _state = mutableStateOf<SearchResultsState>(SearchResultsState.PerformingSearch)
    val state: State<SearchResultsState> = _state
}