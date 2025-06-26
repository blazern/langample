package blazern.langample.data.tatoeba

import blazern.langample.domain.model.Lang
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.data.tatoeba.model.api.ApiResponse
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Sentence
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.io.IOException

class TatoebaClient(
    private val ktorClientHolder: KtorClientHolder,
)  {
    suspend fun search(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): Result<List<TranslationsSet>> {
        val url = "https://tatoeba.org/en/api_v0/search"
        val response: ApiResponse = try {
            ktorClientHolder.client.get(url) {
                parameter("from", langFrom.iso3)
                parameter("to", langTo.iso3)
                parameter("trans_to", langTo.iso3)
                parameter("query", query)
                parameter("trans_filter", "limit")
                parameter("trans_link", "direct")
            }.body()
        } catch (e: IOException) {
            return Result.failure(e)
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
            result.add(TranslationsSet(
                original = Sentence(
                    sentenceTatoeba.text,
                    langTo,
                    DataSource.TATOEBA,
                ),
                translations = translations,
            ))
        }
        return Result.success(result)
    }
}