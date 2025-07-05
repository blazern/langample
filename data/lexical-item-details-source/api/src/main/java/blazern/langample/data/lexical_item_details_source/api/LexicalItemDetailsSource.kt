package blazern.langample.data.lexical_item_details_source.api

import arrow.core.Either
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.Flow

typealias LexicalItemDetailsFlow = Flow<Either<Exception, LexicalItemDetail>>

interface LexicalItemDetailsSource {
    val source: DataSource
    val types: List<LexicalItemDetail.Type>
    fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow
}
