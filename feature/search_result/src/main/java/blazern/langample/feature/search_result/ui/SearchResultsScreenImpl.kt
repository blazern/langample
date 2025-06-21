package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.data.tatoeba.model.SentencesPair
import blazern.langample.feature.search_result.SearchResultsState
import blazern.langample.feature.search_result.SearchResultsViewModel
import blazern.langample.theme.LangampleTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SearchResultsScreenImpl(
    query: String,
    state: State<SearchResultsState>,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = state.value) {
                SearchResultsState.PerformingSearch -> {}
                SearchResultsState.Error -> TODO()
                is SearchResultsState.Results -> {
                    LazyColumn {
                        items(state.examples.size) { index ->
                            val example = state.examples[index]
                            SentencesCard(
                                sentences = listOf(
                                    SentenceData(
                                        text = example.original,
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        textColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    SentenceData(
                                        text = example.translated,
                                        backgroundColor = MaterialTheme.colorScheme.secondary,
                                        textColor = MaterialTheme.colorScheme.onSecondary,
                                    ),
                                ),
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        if (state.value is SearchResultsState.PerformingSearch) {
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
private fun Preview() {
    val state = SearchResultsState.Results(
        explanation = "Hund is Dog",
        examples = listOf(
            SentencesPair("The dog barks", "Der Hund bellt"),
            SentencesPair("The dog sits", "Der Hund sitzt"),
            SentencesPair(
                "A dog sits on a sofa and looks at me",
                "Ein Hund sitzt auf dem Sofa und guckt mich an",
            ),
        )
    )
    val stateObject = remember { mutableStateOf<SearchResultsState>(state) }
    LangampleTheme {
        SearchResultsScreenImpl("Dogs", stateObject)
    }
}
