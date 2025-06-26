package blazern.langample.feature.search_result.ui

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.SearchResultsState
import blazern.langample.theme.LangampleTheme

@Composable
fun FoundSearchResults(
    state: SearchResultsState.Results,
    modifier: Modifier = Modifier,
) {
    val translations = state.examples
    Box(modifier = modifier) {
        Column {
            Box(Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    state.formsHtml,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp))
            }
            Box(Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    state.explanation,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(16.dp))
            }
            LazyColumn {
                items(translations.size) { index ->
                    val example = translations[index]
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


@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun Preview() {
    val state = SearchResultsState.Results(
        formsHtml = "Hund, -e",
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
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FoundSearchResults(state, Modifier.padding(innerPadding))
        }
    }
}
