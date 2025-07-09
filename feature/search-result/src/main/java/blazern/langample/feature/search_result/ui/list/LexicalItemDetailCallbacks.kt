package blazern.langample.feature.search_result.ui.list

import blazern.langample.feature.search_result.model.LexicalItemDetailState

internal interface LexicalItemDetailCallbacks {
    fun onTextCopy(text: String)
    fun onLoadingDetailVisible(loading: LexicalItemDetailState.Loading<*>)
    fun onFixErrorRequest(error: LexicalItemDetailState.Error<*>)

    object Stub : LexicalItemDetailCallbacks {
        override fun onTextCopy(text: String) = Unit
        override fun onLoadingDetailVisible(loading: LexicalItemDetailState.Loading<*>) = Unit
        override fun onFixErrorRequest(error: LexicalItemDetailState.Error<*>) = Unit
    }
}
