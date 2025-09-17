package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.core.logging.Log
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsForExamplesProvider
import kotlinx.coroutines.flow.flow

class TatoebaLexicalItemDetailsSource(
    private val tatoebaClient: TatoebaClient,
    private val cacher: LexicalItemDetailsSourceCacher,
    private val formsForExamplesProvider: FormsForExamplesProvider,
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
        val queriesRes = formsForExamplesProvider.requestFor(
            query = query,
            langFrom = langFrom,
            langTo = langTo,
        )
        // Fixme: properly handle errors: send a signal to the clients that some part could not be loaded
        val finalQuery = queriesRes.fold(
            { Log.e(TAG, it) { "No forms" }; query },
            { it.joinToString("|", "(", ")") { "=${it.text}" } }
        )
        var hasNextPage = true
        var page = 1
        while (hasNextPage) {
            val translationsSetsResult = tatoebaClient.search(
                query = finalQuery,
                langFrom = langFrom,
                langTo = langTo,
                page = page,
            )

            val translationsSets = translationsSetsResult.fold(
                { emit(Left(it)); continue },
                { it }
            )
            translationsSets.forEach {
                emit(Right(LexicalItemDetail.Example(
                    translationsSet = it,
                    source = source,
                )))
            }
            page += 1
            hasNextPage = translationsSets.isNotEmpty()
        }
    }
}

private const val TAG = "TatoebaLexicalItemDetailsSource"