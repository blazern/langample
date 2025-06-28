package blazern.langample.feature.search_result.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.res.stringResource
import blazern.langample.core.strings.R
import blazern.langample.feature.search_result.SearchResultsState
import kotlinx.coroutines.launch

@Composable
internal fun SearchResultsScreen(
    query: String,
    state: SearchResultsState,
    onTextCopy: (String, Clipboard)->Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMsg = stringResource(R.string.general_copied_to_clipboard)
    val onTextCopyWrapper = { text: String, clipboard: Clipboard ->
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
    ) { innerPadding ->
        when (val state = state) {
            SearchResultsState.PerformingSearch -> Loading()
            SearchResultsState.Error -> TODO()
            is SearchResultsState.Results -> {
                FoundSearchResults(
                    state = state,
                    onTextCopy = onTextCopyWrapper,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}
