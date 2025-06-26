package blazern.langample.feature.search_result.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blazern.langample.feature.search_result.SearchResultsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchResultsRoute(
    query: String,
) {
    val viewModel: SearchResultsViewModel = koinViewModel(
        key = query,
        parameters = { parametersOf(query) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    SearchResultsScreen(query, uiState)
}
