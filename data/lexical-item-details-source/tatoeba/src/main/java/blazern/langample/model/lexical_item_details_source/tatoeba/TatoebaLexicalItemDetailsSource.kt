package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.flow

class TatoebaLexicalItemDetailsSource(
    private val tatoebaClient: TatoebaClient,
    private val cacher: LexicalItemDetailsSourceCacher,
) : LexicalItemDetailsSource {
    override val source = DataSource.TATOEBA
    override val types = listOf(LexicalItemDetail.Type.EXAMPLE)

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow = cacher.retrieveOrExecute(source, query, langFrom, langTo) {
        requestImpl(query, langFrom, langTo)
    }

    private fun requestImpl(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow = flow {
        while (true) {
            val translationsSetsResult = tatoebaClient.search(
                query = query,
                langFrom = langFrom,
                langTo = langTo,
            )

            translationsSetsResult.fold(
                { emit(Left(it)) },
                { translationsSets ->
                    translationsSets.forEach {
                        emit(
                            Right(
                                LexicalItemDetail.Example(
                                    translationsSet = it,
                                    source = source,
                                )
                            )
                        )
                    }
                    return@flow
                }
            )
        }
    }
}
