package blazern.langample.feature.search_result.model

import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail

internal sealed class LexicalItemDetailState<T : LexicalItemDetail>(
    open val source: DataSource,
) {
    data class Loading<T : LexicalItemDetail>(
        val type: LexicalItemDetail.Type,
        override val source: DataSource,
        val id: Any = Any(),
    ) : LexicalItemDetailState<T>(source)

    data class Loaded<T : LexicalItemDetail>(
        val detail: T,
    ) : LexicalItemDetailState<T>(detail.source)

    data class Error<T : LexicalItemDetail>(
        val exception: Exception,
        override val source: DataSource,
    ) : LexicalItemDetailState<T>(source)
}
