package blazern.langample.data.tatoeba

import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.tatoeba.model.SentencesPair
import blazern.langample.data.tatoeba.model.api.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TatoebaClient @Inject constructor(
    private val ktorClientHolder: KtorClientHolder,
)  {
    suspend fun search(
        query: String,
        langFrom: String,
        langTo: String,
    ): List<SentencesPair> {
        // TODO: network errors
        val url = "https://tatoeba.org/en/api_v0/search"
        val response: ApiResponse = ktorClientHolder.client.get(url) {
            parameter("from", langFrom)
            parameter("to", langTo)
            parameter("query", query)
            parameter("trans_filter", "limit")
            parameter("trans_link", "direct")
            parameter("trans_to", "deu")
        }.body()

        val result = mutableListOf<SentencesPair>()
        for (sentence in response.results) {
            for (translation in sentence.translations.flatten()) {
                result.add(SentencesPair(sentence.text, translation.text))
            }
        }
        return result
    }
}