package blazern.langample.feature.search_result.ui

import blazern.langample.domain.model.Lang
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.SearchResultsState
import blazern.langample.theme.LangampleTheme

@Composable
internal fun SearchResultsScreen(
    query: String,
    state: SearchResultsState,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = state) {
                SearchResultsState.PerformingSearch -> {}
                SearchResultsState.Error -> TODO()
                is SearchResultsState.Results -> {
                    LazyColumn {
                        items(state.examples.size) { index ->
                            val example = state.examples[index]
                            SentencesCard(
                                sentences = listOf(
                                    SentenceData(
                                        text = example.original.text,
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        textColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    *example.translations.map {
                                        SentenceData(
                                            text = it.text,
                                            backgroundColor = MaterialTheme.colorScheme.secondary,
                                            textColor = MaterialTheme.colorScheme.onSecondary,
                                        )
                                    }.toTypedArray(),
                                ),
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        if (state is SearchResultsState.PerformingSearch) {
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
            TranslationsSet(
                Sentence("The dog barks", Lang.EN),
                listOf(Sentence("Der Hund bellt", Lang.DE)),
            ),
            TranslationsSet(
                Sentence("The dog sits", Lang.EN),
                listOf(
                    Sentence("Der Hund sitzt", Lang.DE),
                    Sentence("Собака сидит", Lang.RU),
                ),
            ),
            TranslationsSet(
                Sentence("A dog sits on a sofa and looks at me", Lang.EN),
                listOf(Sentence("Ein Hund sitzt auf dem Sofa und guckt mich an", Lang.DE)),
            ),
        )
    )
    LangampleTheme {
        SearchResultsScreen("Dogs", state)
    }
}
