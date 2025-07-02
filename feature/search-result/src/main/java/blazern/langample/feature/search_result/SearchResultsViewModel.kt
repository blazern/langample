package blazern.langample.feature.search_result

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class SearchResultsViewModel(
    startQuery: String,
    private val langFrom: Lang,
    private val langTo: Lang,
    private val dataSources: List<LexicalItemDetailsSource>,
) : ViewModel() {
    private val _state = MutableStateFlow<SearchResultsState>(SearchResultsState.PerformingSearch)
    val state: StateFlow<SearchResultsState> = _state

    init {
        search(startQuery)
    }

    private fun search(query: String) {
        val futureResults = dataSources.map {
            it.request(query, langFrom, langTo)
        }.flatten()
        for (futureResult in futureResults) {
            viewModelScope.launch {
                futureResult.details.collect {
                    onNewLexicalDetail(it)
                }
            }
        }
    }

    private fun onNewLexicalDetail(lexicalDetailRes: Either<Exception, LexicalItemDetail>) {
        val oldState = when (val state = _state.value) {
            SearchResultsState.Error -> TODO()
            SearchResultsState.PerformingSearch -> SearchResultsState.Results(
                formsHtml = "",
                formsSource = DataSource.CHATGPT,
                explanation = "",
                explanationSource = DataSource.CHATGPT,
                examples = emptyList(),
            )
            is SearchResultsState.Results -> state
        }

        val lexicalDetail = lexicalDetailRes.fold(
            {
                _state.value = oldState.copy(
                    formsHtml = it.toString(),
                    explanation = it.toString(),
                )
                return
            },
            { it }
        )
        _state.value = when (lexicalDetail) {
            is LexicalItemDetail.Forms -> {
                oldState.copy(
                    formsHtml = lexicalDetail.text,
                    formsSource = lexicalDetail.source,
                )
            }
            is LexicalItemDetail.Explanation -> {
                oldState.copy(
                    explanation = lexicalDetail.text,
                    explanationSource = lexicalDetail.source,
                )
            }
            is LexicalItemDetail.Example -> {
                oldState.copy(
                    examples = oldState.examples + lexicalDetail.translationsSet
                )
            }
        }
    }

    fun copyText(text: String, clipboard: Clipboard) {
        viewModelScope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(
                text, text
            )))
        }
    }
}
