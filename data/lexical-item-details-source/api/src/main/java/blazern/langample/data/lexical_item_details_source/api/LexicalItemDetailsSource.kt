package blazern.langample.data.lexical_item_details_source.api

import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.Flow

interface LexicalItemDetailsSource {
    val source: DataSource
    val types: List<LexicalItemDetail.Type>

fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): Flow<Item>

    sealed interface Item {
        data class Failure(val err: Err) : Item
        data class Page(
            val details: List<LexicalItemDetail>,
            val nextPageTypes: List<LexicalItemDetail.Type>,
            val errors: List<Err> = emptyList(),
        ) : Item {
            init {
                require(nextPageTypes.isNotEmpty())
            }
        }
    }
}
