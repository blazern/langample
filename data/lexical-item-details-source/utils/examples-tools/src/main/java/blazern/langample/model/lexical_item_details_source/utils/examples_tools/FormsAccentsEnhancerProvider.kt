package blazern.langample.model.lexical_item_details_source.utils.examples_tools

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.Lang

class FormsAccentsEnhancerProvider(
    private val formsProvider: FormsForExamplesProvider,
) {
    suspend fun provideFor(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): Either<Err, FormsAccentsEnhancer> {
        return formsProvider.requestFor(query, langFrom, langTo).fold(
            { Left(it) },
            { Right(FormsAccentsEnhancer(it)) }
        )
    }
}
