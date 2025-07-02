package blazern.langample.data.lexical_item_details_source.api

import arrow.core.Either
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.Flow

class FutureLexicalItemDetails(
    val details: Flow<Either<Exception, LexicalItemDetail>>,
    val type: LexicalItemDetail.Type,
    val sources: List<DataSource>,
)

interface LexicalItemDetailsSource {
    fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): List<FutureLexicalItemDetails>
}
