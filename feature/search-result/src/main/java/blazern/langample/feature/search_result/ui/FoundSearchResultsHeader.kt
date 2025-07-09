package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.theme.LangampleTheme
import java.io.IOException

@Composable
internal fun FoundSearchResultsHeader(
    state: SearchResultsState,
    onTextCopy: (String) -> Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
) {
    LazyColumn {
        items(state.forms.size + state.explanations.size) { index ->
            if (index < state.forms.size) {
                FormsStateBlock(state.forms[index], onTextCopy, onLoadingDetailVisible)
            } else {
                val explanation = state.explanations[index - state.forms.size]
                ExplanationStateBlock(explanation, onTextCopy, onLoadingDetailVisible)
            }
        }
    }
}

@Composable
private fun FormsStateBlock(
    state: LexicalItemDetailState<Forms>,
    onTextCopy: (String) -> Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
) {
    when (state) {
        is LexicalItemDetailState.Error<Forms> -> {
            ErrorBlock(state.exception, state.source, onTextCopy)
        }
        is LexicalItemDetailState.Loaded<Forms> -> {
            FormsBlock(state.detail, onTextCopy)
        }
        is LexicalItemDetailState.Loading<Forms> -> {
            onLoadingDetailVisible(state)
            LoadingBlock(state)
        }
    }
}

@Composable
private fun ExplanationStateBlock(
    state: LexicalItemDetailState<Explanation>,
    onTextCopy: (String) -> Unit,
    onLoadingDetailVisible: (LexicalItemDetailState.Loading<*>) -> Unit,
) {
    when (state) {
        is LexicalItemDetailState.Error<Explanation> -> {
            ErrorBlock(state.exception, state.source, onTextCopy)
        }
        is LexicalItemDetailState.Loaded<Explanation> -> {
            ExplanationBlock(state.detail, onTextCopy)
        }
        is LexicalItemDetailState.Loading<Explanation> -> {
            onLoadingDetailVisible(state)
            LoadingBlock(state)
        }
    }
}


@Composable
private fun LoadingBlock(state: LexicalItemDetailState.Loading<*>) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        Row {
            CircularProgressIndicator()
            // TODO: no
            Text("${state.source} ${state.type}")
        }
    }
}

@Composable
private fun BlockWithText(
    text: String,
    backColor: Color,
    textColor: Color,
    source: DataSource,
    onTextCopy: (String) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(backColor)
            .clickable { onTextCopy(text) }
    ) {
        Text(
            text,
            color = textColor,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        SourceLabel(source, textColor)
    }
}

@Composable
private fun ErrorBlock(
    exception: Exception,
    source: DataSource,
    onTextCopy: (String) -> Unit,
) {
    BlockWithText(
        exception.toString(),
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.onError,
        source,
        onTextCopy,
    )
}

@Composable
private fun FormsBlock(
    forms: Forms,
    onTextCopy: (String) -> Unit,
) {
    BlockWithText(
        forms.text,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onPrimary,
        forms.source,
        onTextCopy,
    )
}

@Composable
private fun ExplanationBlock(
    explanation: Explanation,
    onTextCopy: (String) -> Unit,
) {
    BlockWithText(
        explanation.text,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onPrimary,
        explanation.source,
        onTextCopy,
    )
}


@Preview
@Composable
private fun Preview() {
    val state = SearchResultsState(
        forms = listOf(
            LexicalItemDetailState.Loaded(
                detail = Forms("der Hund, -e", DataSource.CHATGPT)
            ),
            LexicalItemDetailState.Loading(
                LexicalItemDetail.Type.FORMS,
                DataSource.CHATGPT,
            ),
        ),
        explanations = listOf(
            LexicalItemDetailState.Loaded(
                detail = Explanation("Hund is Dog", DataSource.CHATGPT)
            ),
            LexicalItemDetailState.Error(
                IOException("No internet"),
                source = DataSource.CHATGPT,
            )
        )
    )
    LangampleTheme {
        FoundSearchResultsHeader(
            state,
            {},
            {},
        )
    }
}