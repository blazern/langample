package blazern.langample.data.lexical_item_details_source.kaikki

import blazern.langample.data.kaikki.KaikkiClient
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource.Item
import blazern.langample.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class KaikkiLexicalItemDetailsSource(
    private val kaikkiClient: KaikkiClient,
    private val cacher: LexicalItemDetailsSourceCacher,
) : LexicalItemDetailsSource {
    override val source = DataSource.KAIKKI
    override val types = listOf(
        LexicalItemDetail.Type.FORMS,
        LexicalItemDetail.Type.EXPLANATION,
        LexicalItemDetail.Type.EXAMPLE,
    )

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): Flow<Item> = cacher.retrieveOrExecute(source, query, langFrom, langTo) {
        requestImpl(query, langFrom, langTo)
    }

    private fun requestImpl(
        query: String,
        langFrom: Lang,
        langTo: Lang,
        depth: Int = 0,
    ): Flow<Item> {
        return flow {
            var entries: List<Entry>? = null
            do {
                entries = kaikkiClient.search(query, langFrom).fold(
                    { emit(Item.Failure(it)); null },
                    { it }
                )
            } while (entries == null)

            for (entry in entries) {
                val formsOf = entry.senses.map { it.formOf }.flatten()
                val purelyWordForm = formsOf.size == entry.senses.size
                if (!purelyWordForm) {
                    val details = entry.toDetails(langFrom, langTo)
                    val page = Item.Page(
                        details = details,
                        nextPageTypes = types,
                    )
                    emit(page)
                }
                if (depth == 0) {
                    formsOf.forEach {
                        emitAll(requestImpl(it.word, langFrom, langTo, depth + 1))
                    }
                }
            }
        }
    }
}
