package blazern.langample.feature.search_result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import blazern.langample.theme.LangampleTheme

@Composable
fun SearchResultsScreen(
    viewModel: SearchResultsViewModel = hiltViewModel()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (viewModel.state.value) {
                SearchResultsState.PerformingSearch -> {}
                SearchResultsState.Error -> TODO()
                is SearchResultsState.Results -> TODO()
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
        SearchResultsScreen()
    }
}
