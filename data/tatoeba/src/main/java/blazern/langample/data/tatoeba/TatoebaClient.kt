package blazern.langample.data.tatoeba

import Lang
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.data.tatoeba.model.api.ApiResponse
import blazern.langample.domain.model.Sentence
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

// TODO: test
class TatoebaClient(
    private val ktorClientHolder: KtorClientHolder,
)  {
    suspend fun search(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): List<TranslationsSet> {
        // TODO: network errors
        val url = "https://tatoeba.org/en/api_v0/search"
        val response: ApiResponse = ktorClientHolder.client.get(url) {
            parameter("from", langTo.iso3)
            parameter("to", langFrom.iso3)
            parameter("trans_to", langFrom.iso3)
            parameter("query", query)
            parameter("trans_filter", "limit")
            parameter("trans_link", "direct")
        }.body()

        val result = mutableListOf<TranslationsSet>()
        for (sentenceTatoeba in response.results) {
            val translations = mutableListOf<Sentence>()
            for (translationTatoeba in sentenceTatoeba.translations.flatten()) {
                if (translationTatoeba.lang == langFrom.iso3) {
                    translations.add(Sentence(translationTatoeba.text, langFrom))
                }
            }
            result.add(TranslationsSet(
                original = Sentence(sentenceTatoeba.text, langTo),
                translations = translations,
            ))
        }
        return result
    }
}