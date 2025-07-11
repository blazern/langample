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
        val size = state.forms.size + state.explanations.size + state.examples.size
        LazyColumn {
            items(size) { index ->
                when {
                    index < state.forms.size -> {
                        val forms = state.forms[index]
                        ListItem(
                            forms,
                            callbacks,
                            Modifier.fillMaxWidth(),
                        )
                    }
                    index < state.forms.size + state.explanations.size -> {
                        val explanation = state.explanations[index - state.forms.size]
                        ListItem(
                            explanation,
                            callbacks,
                            Modifier.fillMaxWidth(),
                        )
                    }
                    else -> {
                        val example = state.examples[index - state.forms.size - state.explanations.size]
                        Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp)) {
                            ListItem(
                                example,
                                callbacks,
                                Modifier.fillMaxWidth(),
                            )
                        }
                    }
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
