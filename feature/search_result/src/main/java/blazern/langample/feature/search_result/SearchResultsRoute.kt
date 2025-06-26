package blazern.langample.feature.search_result

import blazern.langample.domain.model.Lang
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blazern.langample.feature.search_result.ui.SearchResultsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchResultsRoute(
    query: String,
    langFrom: Lang,
    langTo: Lang,
) {
    val viewModel: SearchResultsViewModel = koinViewModel(
        key = query,
        parameters = { parametersOf(query, langFrom, langTo) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    SearchResultsScreen(query, uiState)
}
