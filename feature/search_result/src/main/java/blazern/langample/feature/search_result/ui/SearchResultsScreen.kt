package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import blazern.langample.feature.search_result.SearchResultsState

@Composable
internal fun SearchResultsScreen(
    query: String,
    state: SearchResultsState,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (val state = state) {
            SearchResultsState.PerformingSearch -> Loading()
            SearchResultsState.Error -> TODO()
            is SearchResultsState.Results -> {
                FoundSearchResults(state, Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}
