package blazern.langample.data.tatoeba

import arrow.core.Either
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.tatoeba.model.api.ApiResponse
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TatoebaClient(
    private val ktorClientHolder: KtorClientHolder,
)  {
    @Suppress("TooGenericExceptionCaught")
    suspend fun search(
        query: String,
        langFrom: Lang,
        langTo: Lang,
        page: Int,
    ): Either<Exception, List<TranslationsSet>> {
        val url = "https://tatoeba.org/en/api_v0/search"
        val response: ApiResponse = try {
            ktorClientHolder.client.get(url) {
                parameter("from", langFrom.iso3)
                parameter("to", langTo.iso3)
                parameter("trans_to", langTo.iso3)
                parameter("query", query)
                parameter("trans_filter", "limit")
                parameter("trans_link", "direct")
                parameter("page", page)
            }.body()
        } catch (e: Exception) {
            return Either.Left(e)
        }

        val result = mutableListOf<TranslationsSet>()
        for (sentenceTatoeba in response.results) {
            val translations = mutableListOf<Sentence>()
            for (translationTatoeba in sentenceTatoeba.translations.flatten()) {
                if (translationTatoeba.lang == langTo.iso3) {
                    translations.add(Sentence(
                        text = translationTatoeba.text,
                        lang = langTo,
                        source = DataSource.TATOEBA,
                    ))
                }
            }
            if (translations.isNotEmpty()) {
                result.add(TranslationsSet(
                    original = Sentence(
                        text = sentenceTatoeba.text,
                        lang = langFrom,
                        source = DataSource.TATOEBA,
                    ),
                    translations = translations,
                    translationsQualities = translations.map { TranslationsSet.QUALITY_MAX }
                ))
            }
        }
        return Either.Right(result)
    }
}
