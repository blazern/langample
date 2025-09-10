package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import blazern.langample.core.strings.R
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.LexicalItemDetailState.Error
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.feature.search_result.ui.list.LexicalItemDetailCallbacks
import blazern.langample.theme.LangampleTheme
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
internal fun SearchResultsScreen(
    query: String,
    state: SearchResultsState,
    onTextCopy: (String, Clipboard)->Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
    onFixErrorRequest: (Error<*>) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMsg = stringResource(R.string.general_copied_to_clipboard)
    val clipboard = LocalClipboard.current
    val onTextCopyWrapper = { text: String ->
        onTextCopy.invoke(text, clipboard)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(copiedMsg)
        }
        Unit
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                .zIndex(1f), // Because items of [FoundSearchResults] somehow are drawn on top otherwise
            ) {
                FlowRow {
                    Text(
                        stringResource(R.string.search_results_title) + " ",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        query,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            val callbacks = object : LexicalItemDetailCallbacks {
                override fun onTextCopy(text: String) = onTextCopyWrapper(text)
                override fun onLoadingDetailVisible(loading: LexicalItemDetailState.Loading<*>) =
                    onLoadingDetailVisible(loading)
                override fun onFixErrorRequest(error: Error<*>) = onFixErrorRequest(error)
            }
            FoundSearchResults(
                state = state,
                callbacks = callbacks,
            )
        }
    }
}


@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun PreviewAllGood() {
    val state = SearchResultsState(
        forms = listOf(LexicalItemDetailState.Loaded(Forms(
            Forms.Value.Text("der Hund, -e"),
            DataSource.CHATGPT),
        )),
        explanations = listOf(LexicalItemDetailState.Loaded(Explanation("Hund is Dog", DataSource.CHATGPT))),
        examples = listOf(
            LexicalItemDetailState.Loaded(
                Example(
                TranslationsSet(
                    Sentence("The dog barks", Lang.EN, DataSource.TATOEBA),
                    listOf(Sentence("Der Hund bellt", Lang.DE, DataSource.TATOEBA)),
                    listOf(TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )
            ),
            LexicalItemDetailState.Loaded(
                Example(
                TranslationsSet(
                    Sentence("The dog sits", Lang.EN, DataSource.CHATGPT),
                    listOf(
                        Sentence("Der Hund sitzt", Lang.DE, DataSource.CHATGPT),
                        Sentence("Собака сидит", Lang.RU, DataSource.CHATGPT),
                    ),
                    listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )
            ),
            LexicalItemDetailState.Loaded(
                Example(
                TranslationsSet(
                    Sentence("A dog sits on a sofa and looks at me", Lang.EN, DataSource.CHATGPT),
                    listOf(
                        Sentence(
                            "Ein Hund sitzt auf dem Sofa und guckt mich an",
                            Lang.DE,
                            DataSource.CHATGPT,
                        )
                    ),
                    listOf(TranslationsSet.QUALITY_MAX, TranslationsSet.QUALITY_MAX),
                ),
                DataSource.CHATGPT,
            )
            )
        ),
    )
    LangampleTheme {
        SearchResultsScreen(
            query = "Hund",
            state = state,
            onTextCopy = { _, _ -> },
            onLoadingDetailVisible = {},
            onFixErrorRequest = {},
        )
    }
}


@PreviewScreenSizes
@Preview(device = PIXEL_3A_XL, name = "400x500", heightDp = 400, widthDp = 500)
@Composable
private fun PreviewErrors() {
    val state = SearchResultsState(
        forms = listOf(Error(IOException("Not internet connection"), DataSource.CHATGPT)),
        explanations = listOf(Error(IOException("Not internet connection"), DataSource.CHATGPT)),
        examples = listOf(
            Error(IOException("Not internet connection"), DataSource.TATOEBA),
            Error(IOException("Not internet connection"), DataSource.CHATGPT)
        ),
    )
    LangampleTheme {
        SearchResultsScreen(
            query = "Herangehensweise",
            state = state,
            onTextCopy = { _, _ -> },
            onLoadingDetailVisible = {},
            onFixErrorRequest = {},
        )
    }
}
