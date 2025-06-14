package blazern.langample.feature.search_result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import blazern.langample.theme.LangampleTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchResultsScreen(
    query: String,
    viewModel: SearchResultsViewModel = koinViewModel()
) {
    // TODO: probably a bad idea
    LaunchedEffect(query) {
        viewModel.search(query)
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = viewModel.state.value) {
                SearchResultsState.PerformingSearch -> {}
                SearchResultsState.Error -> TODO()
                is SearchResultsState.Results -> {
                    LazyColumn {
                        items(state.examples.size) { index ->
                            val example = state.examples[index]
                            Text("${example.original} - ${example.translated}")
                        }
                    }
                }
            }
        }
        if (viewModel.state.value is SearchResultsState.PerformingSearch) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
fun Preview() {
    LangampleTheme {
        SearchResultsScreen("Cats")
    }
}
