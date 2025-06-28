package blazern.langample.feature.search_result

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.feature.search_result.llm.LLMWordExplanation
import blazern.langample.feature.search_result.usecase.ChatGPTWordSearchUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class SearchResultsViewModel(
    startQuery: String,
    private val langFrom: Lang,
    private val langTo: Lang,
    private val tatoebaClient: TatoebaClient,
    private val chatGPTUseCase: ChatGPTWordSearchUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<SearchResultsState>(SearchResultsState.PerformingSearch)
    val state: StateFlow<SearchResultsState> = _state

    init {
        search(startQuery)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            val examplesAsync = async { tatoebaClient.search(query, langFrom, langTo) }
            val chatGptResponseAsync = async { chatGPTUseCase.invoke(query, langFrom, langTo) }

            var examples = examplesAsync.await().getOrElse { emptyList() }
            val chatGptResponse = chatGptResponseAsync.await().getOrElse {
                LLMWordExplanation(it.message ?: "$it", "-", emptyList())
            }

            examples = examples + chatGptResponse.examples
            val formsHtml = chatGptResponse.formsHtml
            val explanation = chatGptResponse.explanation
            _state.value = SearchResultsState.Results(
                formsHtml = formsHtml,
                formsSource = DataSource.CHATGPT,
                explanation = explanation,
                explanationSource = DataSource.CHATGPT,
                examples = examples,
            )
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
