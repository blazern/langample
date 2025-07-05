package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.theme.LangampleTheme

@Composable
internal fun FoundSearchResults(
    state: SearchResultsState,
    onTextCopy: (String)->Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
    onFixErrorRequest: (LexicalItemDetailState.Error<*>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val translations = state.examples
    Box(modifier = modifier) {
        Column {
            FoundSearchResultsHeader(state, onTextCopy, onLoadingDetailVisible)
            FoundSearchResultsSentences(translations, onTextCopy, onLoadingDetailVisible, onFixErrorRequest)
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
                onTextCopy = {},
                onLoadingDetailVisible = {},
                onFixErrorRequest = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
