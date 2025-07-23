package blazern.langample.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blazern.langample.domain.model.Lang
import blazern.langample.feature.home.ui.HomeScreen
import blazern.langample.feature.home.ui.HomeScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

typealias SearchFn = (query: String, langFrom: Lang, langTo: Lang)->Unit

@Composable
fun HomeRoute(onSearch: SearchFn) {
    val startQuery = ""
    val viewModel: HomeScreenViewModel = koinViewModel(
        parameters = { parametersOf(startQuery) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        uiState,
        viewModel::onQueryChange,
        viewModel::onLangsChange,
        onSearch,
    )
}

