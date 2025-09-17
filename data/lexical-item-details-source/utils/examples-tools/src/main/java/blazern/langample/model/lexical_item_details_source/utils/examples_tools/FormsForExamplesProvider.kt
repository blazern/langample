package blazern.langample.model.lexical_item_details_source.utils.examples_tools

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.WordForm
import kotlinx.coroutines.flow.firstOrNull

class FormsForExamplesProvider(
    private val kaikki: KaikkiLexicalItemDetailsSource,
) {
    suspend fun requestFor(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): Either<Exception, List<WordForm>> {
        val flow = kaikki.request(query, langFrom, langTo)
        val formsRes = flow.firstOrNull {
            it.fold(
                { true },
                { it is LexicalItemDetail.Forms }
            )
        }
        val forms = formsRes?.getOrElse {
            return Either.Left(it)
        } as LexicalItemDetail.Forms?
            ?: return Either.Left(IllegalStateException("No Forms returned from Kaikki"))

        val formsVal = forms.value
        if (formsVal !is LexicalItemDetail.Forms.Value.Detailed) {
            return Either.Left(IllegalStateException("Forms without Value: $forms"))
        }
        return Either.Right(formsVal.forms.toFormsForExamples())
    }
}

private fun List<WordForm>.toFormsForExamples() =
    map { it.withoutPronoun().withoutArticle() }
        .distinct()
        .filter { it.wordsCount == 1 && !it.auxiliary }
