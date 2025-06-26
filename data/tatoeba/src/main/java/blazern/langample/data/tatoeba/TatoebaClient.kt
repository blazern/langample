package blazern.langample.data.tatoeba

import Lang
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.tatoeba.model.SentencesPair
import blazern.langample.data.tatoeba.model.api.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TatoebaClient(
    private val ktorClientHolder: KtorClientHolder,
)  {
    suspend fun search(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): List<SentencesPair> {
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

        val result = mutableListOf<SentencesPair>()
        for (sentence in response.results) {
            for (translation in sentence.translations.flatten()) {
                if (translation.lang == langFrom.iso3) {
                    result.add(SentencesPair(sentence.text, translation.text))
                }
            }
        }
        return result
    }
}