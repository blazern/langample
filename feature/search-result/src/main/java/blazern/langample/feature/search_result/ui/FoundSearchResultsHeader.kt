package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.unit.dp
import blazern.langample.feature.search_result.SearchResultsState

@Composable
internal fun FoundSearchResultsHeader(
    state: SearchResultsState.Results,
    clipboard: Clipboard,
    onTextCopy: (String, Clipboard) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onTextCopy(state.formsHtml, clipboard) }
    ) {
        Text(
            state.formsHtml,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(16.dp)
        )
        SourceLabel(state.formsSource, MaterialTheme.colorScheme.onPrimary)
    }
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onTextCopy(state.explanation, clipboard) }
    ) {
        Text(
            state.explanation,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(16.dp)
        )
        SourceLabel(state.explanationSource, MaterialTheme.colorScheme.onSecondary)
    }
}
