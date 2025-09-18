package blazern.langample.feature.search_result.model

import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal sealed class LexicalItemDetailState<T : LexicalItemDetail> (
    open val source: DataSource,
    val id: String = Uuid.random().toString(),
) {
    data class Loading<T : LexicalItemDetail>(
        val type: LexicalItemDetail.Type,
        override val source: DataSource,
    ) : LexicalItemDetailState<T>(source)

    data class Loaded<T : LexicalItemDetail>(
        val detail: T,
    ) : LexicalItemDetailState<T>(detail.source)

    data class Error<T : LexicalItemDetail>(
        val err: Err,
        override val source: DataSource,
    ) : LexicalItemDetailState<T>(source)
}
