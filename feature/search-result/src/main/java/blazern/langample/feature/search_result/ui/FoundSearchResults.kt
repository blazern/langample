package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.feature.search_result.ui.list.LexicalItemDetailCallbacks
import blazern.langample.feature.search_result.ui.list.ListItem
import blazern.langample.theme.LangampleTheme

@Composable
internal fun FoundSearchResults(
    state: SearchResultsState,
    callbacks: LexicalItemDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(state.forms.size) { index ->
                ListItem(
                    state.forms[index],
                    callbacks,
                    Modifier.fillMaxWidth(),
                )
            }
            items(state.wordTranslations.size) { index ->
                ListItem(
                    state.wordTranslations[index],
                    callbacks,
                    Modifier.fillMaxWidth(),
                )
            }
            items(state.synonyms.size) { index ->
                ListItem(
                    state.synonyms[index],
                    callbacks,
                    Modifier.fillMaxWidth(),
                )
            }
            items(state.explanations.size) { index ->
                ListItem(
                    state.explanations[index],
                    callbacks,
                    Modifier.fillMaxWidth(),
                )
            }
            items(state.examples.size) { index ->
                Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp)) {
                    ListItem(
                        state.examples[index],
                        callbacks,
                        Modifier.fillMaxWidth(),
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
    val state = SearchResultsState(
        forms = listOf(LexicalItemDetailState.Loaded(Forms("der Hund, -e", DataSource.CHATGPT))),
        explanations = listOf(LexicalItemDetailState.Loaded(Explanation("Hund is Dog", DataSource.CHATGPT))),
        examples = listOf(
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("The dog barks", Lang.EN, DataSource.TATOEBA),
                    listOf(Sentence("Der Hund bellt", Lang.DE, DataSource.TATOEBA)),
                    listOf(TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )),
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("The dog sits", Lang.EN, DataSource.CHATGPT),
                    listOf(
                        Sentence("Der Hund sitzt", Lang.DE, DataSource.CHATGPT),
                        Sentence("Собака сидит", Lang.RU, DataSource.CHATGPT),
                    ),
                    listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )),
            LexicalItemDetailState.Loaded(Example(
                TranslationsSet(
                    Sentence("A dog sits on a sofa and looks at me", Lang.EN, DataSource.CHATGPT),
                    listOf(Sentence(
                        "Ein Hund sitzt auf dem Sofa und guckt mich an",
                        Lang.DE,
                        DataSource.CHATGPT,
                    )),
                    listOf(TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            ))
        ),
    )
    LangampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FoundSearchResults(
                state = state,
                callbacks = LexicalItemDetailCallbacks.Stub,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
