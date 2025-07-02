package blazern.langample.model.lexical_item_details_source.tatoeba

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.data.lexical_item_details_source.api.FutureLexicalItemDetails
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import kotlinx.coroutines.flow.flow

class TatoebaLexicalItemDetailsSource(
    private val tatoebaClient: TatoebaClient,
) : LexicalItemDetailsSource {
    override val source = DataSource.TATOEBA

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): List<FutureLexicalItemDetails> {
        val translationsFlow = flow<Either<Exception, LexicalItemDetail>> {
            val translationsSetsResult = tatoebaClient.search(
                query = query,
                langFrom = langFrom,
                langTo = langTo,
            )

            val translationsSets = translationsSetsResult.getOrElse {
                emit(Left(it))
                return@flow
            }
            translationsSets.forEach {
                emit(Right(LexicalItemDetail.Example(
                    translationsSet = it,
                    source = source,
                )))
            }
        }
        return listOf(
            FutureLexicalItemDetails(
            details = translationsFlow,
            type = LexicalItemDetail.Type.EXAMPLE,
            source = source,
        ))
    }
}
