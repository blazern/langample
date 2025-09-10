package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.WordForm
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class TatoebaLexicalItemDetailsSource(
    private val tatoebaClient: TatoebaClient,
    private val cacher: LexicalItemDetailsSourceCacher,
    private val kaikki: KaikkiLexicalItemDetailsSource,
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
            // Fixme: properly handle errors: send a signal to the clients that some part could not be loaded
            val formsRes = kaikki.request(query, langFrom, langTo).firstOrNull {
                it.getOrNull() is Forms
            }
            val forms = formsRes?.getOrNull() as Forms?
            val finalQuery = if (forms?.value is Forms.Value.Detailed) {
                val value = forms.value as Forms.Value.Detailed
                value.forms
                    .map { it.withoutPronoun().withoutArticle() }
                    .distinct()
                    .filter { it.wordsCount == 1 && !it.auxiliary }
                    .joinToString("|", "(", ")") { "=${it.withoutPronoun().withoutArticle().text}" }
            } else {
                query
            }

            // Fixme: other pages than 1
            val translationsSetsResult = tatoebaClient.search(
                query = finalQuery,
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
