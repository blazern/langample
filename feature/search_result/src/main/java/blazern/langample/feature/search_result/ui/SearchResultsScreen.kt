package blazern.langample.feature.search_result.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import blazern.langample.feature.search_result.SearchResultsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchResultsScreen(
    query: String,
    viewModel: SearchResultsViewModel = koinViewModel(),
) {
    // TODO: probably a bad idea
    LaunchedEffect(query) {
        viewModel.search(query)
    }
    SearchResultsScreenImpl(query, viewModel.state)
}
