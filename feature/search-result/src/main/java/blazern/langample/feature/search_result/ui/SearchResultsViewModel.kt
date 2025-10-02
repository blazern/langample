package blazern.langample.feature.search_result.ui

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource.Item
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.feature.search_result.model.LexicalItemDetailsGroupState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.feature.search_result.model.addLoadingFor
import blazern.langample.feature.search_result.model.removeAllButLoadedFor
import blazern.langample.feature.search_result.model.removeErrorsFor
import blazern.langample.feature.search_result.model.replaceAllButLoadedWith
import blazern.langample.utils.FlowIterator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private typealias LexicalItemDetailFlow = FlowIterator<Item>

internal class SearchResultsViewModel(
    startQuery: String,
    private val langFrom: Lang,
    private val langTo: Lang,
    private val dataSources: List<LexicalItemDetailsSource>,
) : ViewModel() {
    private val loadingInProgress = mutableSetOf<DataSource>()
    private val dataIters = mutableMapOf<DataSource, LexicalItemDetailFlow>()
    private val sourceTypes = mutableMapOf<DataSource, Set<LexicalItemDetail.Type>>()
    // NOTE: if new fields and responsibilities are being added, extract the 3 fields above and
    // a ton of related methods into a separate class.

    private val _state = MutableStateFlow(SearchResultsState())
    val state: StateFlow<SearchResultsState> = _state

    init {
        viewModelScope.launch {
            search(startQuery)
        }
    }

    private suspend fun search(query: String) {
        _state.value = SearchResultsState()
        for (dataSource in dataSources) {
            val source = dataSource.source
            sourceTypes[source] = dataSource.types.toSet()
            val flow = dataSource.request(query, langFrom, langTo)
            dataIters[source] = FlowIterator(flow)
            continueLoadingFor(source)
        }
    }

    fun copyText(text: String, clipboard: Clipboard) {
        viewModelScope.launch {
            clipboard.setClipEntry(
                ClipEntry(
                    ClipData.newPlainText(
                        text, text
                    )
                )
            )
        }
    }

    fun onLoadingDetailVisible(
        loading: LexicalItemDetailsGroupState.Loading,
    ) {
        continueLoadingFor(loading.source)
    }

    private fun continueLoadingFor(
        source: DataSource,
    ) {
        val iter = dataIters[source] ?: return
        viewModelScope.launch {
            if (loadingInProgress.contains(source) || iter.hasEnded()) {
                return@launch
            }
            loadingInProgress.add(source)
            val sourceTypes = sourceTypes[source].orEmpty()
            _state.value = _state.value
                .removeAllButLoadedFor(source)
                .addLoadingFor(source, sourceTypes)
            val next = iter.next()
            if (next != null) {
                onNextDetailResult(next, source, sourceTypes)
            } else {
                // The end
                _state.value = _state.value.removeAllButLoadedFor(source)
            }
            loadingInProgress.remove(source)
        }
    }

    private fun onNextDetailResult(
        item: Item,
        source: DataSource,
        requestedTypes: Set<LexicalItemDetail.Type>,
    ) {
        var state = _state.value
        when (item) {
            is Item.Failure -> {
                state = state.replaceAllButLoadedWith(item, source, requestedTypes)
            }
            is Item.Page -> {
                val page = transform(item)
                if (page != null) {
                    state = state.replaceAllButLoadedWith(page, source, requestedTypes)
                }
                state = state.addLoadingFor(source, item.nextPageTypes)
                sourceTypes[source] = item.nextPageTypes
            }
        }
        _state.value = state
    }

    private fun transform(page: Item.Page): Item.Page? {
        val transformedDetails = page.details.mapNotNull { transform(it) }
        return if (transformedDetails.isNotEmpty()) {
            page.copy(details = transformedDetails)
        } else {
            null
        }
    }

    private fun transform(detail: LexicalItemDetail): LexicalItemDetail? {
        if (detail is Forms) {
            val value = detail.value
            if (value is Forms.Value.Detailed) {
                val forms = value.forms
                    .map { it.withoutPronoun() }
                    .distinct()
                    .filter {
                        val wordsCount = it.wordsCount
                        val oneWord = wordsCount == 1 || (wordsCount == 2 && it.hasArticle)
                        val onlyLetters = Regex("(\\w| )+").matches(it.text)
                        oneWord && onlyLetters && !it.auxiliary
                    }
                return if (forms.isNotEmpty()) {
                    detail.copy(value = Forms.Value.Detailed(forms))
                } else {
                    null
                }
            }
        }
        return detail
    }

    fun onFixErrorRequest(error: LexicalItemDetailsGroupState.Error) {
        _state.value = _state.value.removeErrorsFor(error.source)
        continueLoadingFor(error.source)
    }
}
