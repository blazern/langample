package blazern.langample.feature.search_result.ui.list

import blazern.langample.feature.search_result.model.LexicalItemDetailsGroupState

internal interface LexicalItemDetailCallbacks {
    fun onTextCopy(text: String)
    fun onLoadingDetailVisible(loading: LexicalItemDetailsGroupState.Loading)
    fun onFixErrorRequest(error: LexicalItemDetailsGroupState.Error)

    object Stub : LexicalItemDetailCallbacks {
        override fun onTextCopy(text: String) = Unit
        override fun onLoadingDetailVisible(loading: LexicalItemDetailsGroupState.Loading) = Unit
        override fun onFixErrorRequest(error: LexicalItemDetailsGroupState.Error) = Unit
    }
}
