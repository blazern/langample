package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.SearchResultsState
import blazern.langample.theme.LangampleTheme

@Composable
internal fun FoundSearchResults(
    state: SearchResultsState.Results,
    onTextCopy: (String, Clipboard)->Unit,
    modifier: Modifier = Modifier,
) {
    val translations = state.examples
    val clipboard = LocalClipboard.current
    Box(modifier = modifier) {
        Column {
            FoundSearchResultsHeader(state, clipboard, onTextCopy)
            FoundSearchResultsSentences(translations, clipboard, onTextCopy)
        }
    }
}

@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun Preview() {
    val state = SearchResultsState.Results(
        formsHtml = "der Hund, -e",
        formsSource = DataSource.CHATGPT,
        explanation = "Hund is Dog",
        explanationSource = DataSource.CHATGPT,
        examples = listOf(
            TranslationsSet(
                Sentence("The dog barks", Lang.EN, DataSource.TATOEBA),
                listOf(Sentence("Der Hund bellt", Lang.DE, DataSource.TATOEBA)),
            ),
            TranslationsSet(
                Sentence("The dog sits", Lang.EN, DataSource.CHATGPT),
                listOf(
                    Sentence("Der Hund sitzt", Lang.DE, DataSource.CHATGPT),
                    Sentence("Собака сидит", Lang.RU, DataSource.CHATGPT),
                ),
            ),
            TranslationsSet(
                Sentence("A dog sits on a sofa and looks at me", Lang.EN, DataSource.CHATGPT),
                listOf(Sentence(
                    "Ein Hund sitzt auf dem Sofa und guckt mich an",
                    Lang.DE,
                    DataSource.CHATGPT,
                )),
            ),
        )
    )
    LangampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FoundSearchResults(
                state = state,
                onTextCopy = { _, _ -> },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
