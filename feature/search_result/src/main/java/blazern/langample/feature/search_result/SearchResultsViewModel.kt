package blazern.langample.feature.search_result

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blazern.langample.data.tatoeba.TatoebaClient
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    private val tatoebaClient: TatoebaClient,
) : ViewModel() {
    private val _state = mutableStateOf<SearchResultsState>(SearchResultsState.PerformingSearch)
    val state: State<SearchResultsState> = _state

    fun search(query: String) {
        viewModelScope.launch {
            // TODO: create a use case?
            val examples = tatoebaClient.search(query, "rus", "deu")
            _state.value = SearchResultsState.Results(
                explanation = query,
                examples = examples,
            )
        }
    }
}