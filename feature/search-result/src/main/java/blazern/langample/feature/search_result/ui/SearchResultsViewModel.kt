package blazern.langample.feature.search_result.ui

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
import blazern.langample.domain.model.toClass
import blazern.langample.feature.search_result.model.LexicalItemDetailState
import blazern.langample.feature.search_result.model.SearchResultsState
import blazern.langample.feature.search_result.model.add
import blazern.langample.feature.search_result.model.remove
import blazern.langample.feature.search_result.model.replaceWithError
import blazern.langample.utils.FlowIterator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private typealias LexicalItemDetailFlow = FlowIterator<Either<Exception, LexicalItemDetail>>

internal class SearchResultsViewModel(
    startQuery: String,
    private val langFrom: Lang,
    private val langTo: Lang,
    private val dataSources: List<LexicalItemDetailsSource>,
) : ViewModel() {
    private val loadingInProgress = mutableSetOf<DataSource>()
    private val dataIters = mutableMapOf<DataSource, LexicalItemDetailFlow>()
    private val sourceTypes = mutableMapOf<DataSource, List<LexicalItemDetail.Type>>()
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
            sourceTypes[source] = dataSource.types
            val flow = dataSource.request(query, langFrom, langTo)
            dataIters[source] = FlowIterator(flow)
            addLoadingsFor<LexicalItemDetail>(source)
        }
    }

    private fun <T : LexicalItemDetail> addLoadingsFor(source: DataSource) {
        var newState = state.value.remove(
            listOf(
                LexicalItemDetailState.Loading::class,
                LexicalItemDetailState.Error::class,
            ),
            source,
        )
        for (type in sourceTypes[source] ?: emptyList()) {
            newState = newState.add(
                type.toClass(),
                LexicalItemDetailState.Loading<T>(type, source)
            )
        }
        _state.value = newState
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

    fun <T : LexicalItemDetail> onLoadingDetailVisible(
        loading: LexicalItemDetailState.Loading<T>,
    ) {
        continueLoadingFor<T>(loading.source)
    }

    private fun <T : LexicalItemDetail> continueLoadingFor(
        source: DataSource,
    ) {
        val iter = dataIters[source] ?: return
        viewModelScope.launch {
            if (loadingInProgress.contains(source) || iter.hasEnded()) {
                return@launch
            }
            loadingInProgress.add(source)
            addLoadingsFor<T>(source)
            val next = iter.next()
            if (next != null) {
                onNextDetailResult<LexicalItemDetail>(next, source)
            } else {
                // The end
                _state.value = _state.value.remove(
                    listOf(LexicalItemDetailState.Loading::class, LexicalItemDetailState.Error::class),
                    source
                )
            }
            loadingInProgress.remove(source)
        }
    }

    private fun <T : LexicalItemDetail> onNextDetailResult(
        detailRes: Either<Exception, LexicalItemDetail>,
        source: DataSource,
    ) {
        detailRes.fold(
            {
                _state.value = _state.value.replaceWithError(
                    source,
                    listOf(LexicalItemDetailState.Loading::class, LexicalItemDetailState.Error::class),
                    { LexicalItemDetailState.Error<T>(it, source) },
                )
            },
            {
                _state.value = _state.value
                    .remove(
                        listOf(LexicalItemDetailState.Loading::class, LexicalItemDetailState.Error::class),
                        source,
                    )
                    .add(it::class, LexicalItemDetailState.Loaded(it))
                    .add(it.type.toClass(),
                        LexicalItemDetailState.Loading<LexicalItemDetail>(it.type, source),
                )
            }
        )
    }

    fun <T : LexicalItemDetail> onFixErrorRequest(error: LexicalItemDetailState.Error<T>) {
        continueLoadingFor<T>(error.source)
    }
}
