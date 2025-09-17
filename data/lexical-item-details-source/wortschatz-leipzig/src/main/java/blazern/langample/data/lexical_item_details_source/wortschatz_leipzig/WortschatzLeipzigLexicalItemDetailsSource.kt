package blazern.langample.data.lexical_item_details_source.wortschatz_leipzig

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.core.logging.Log
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.lexical_item_details_source.wortschatz_leipzig.model.LeipzigSentence
import blazern.langample.data.lexical_item_details_source.wortschatz_leipzig.model.LeipzigSentencesResponse
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsForExamplesProvider
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.flow
import java.net.URLEncoder

class WortschatzLeipzigLexicalItemDetailsSource(
    private val ktorClientHolder: KtorClientHolder,
    private val cacher: LexicalItemDetailsSourceCacher,
    private val formsForExamplesProvider: FormsForExamplesProvider,
) : LexicalItemDetailsSource {

    override val source = DataSource.WORTSCHATZ_LEIPZIG
    override val types = listOf(LexicalItemDetail.Type.EXAMPLE)

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow = cacher.retrieveOrExecute(source, query, langFrom, langTo) {
        requestImpl(query, langFrom, langTo)
    }

    @Suppress("TooGenericExceptionCaught")
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
        val queries = queriesRes.fold(
            { Log.e(TAG, it) { "No forms" }; listOf(query) },
            { it.map { it.text } }
        )

        val baseUrl = "https://api.wortschatz-leipzig.de/ws/sentences"
        for (query in queries) {
            val encodedTerm = encodePathSegment(query)
            val seen = HashSet<String>()
            for (corpus in textCorporaFor(langFrom)) {
                val url = "$baseUrl/$corpus/sentences/$encodedTerm"
                var hasNextPage = true
                var offset = 0
                while (hasNextPage) {
                    var response: LeipzigSentencesResponse? = null
                    while (response == null) {
                        response = try {
                            ktorClientHolder.client.get(url) {
                                parameter("offset", offset)
                                parameter("limit", LIMIT)
                            }.body<LeipzigSentencesResponse>()
                        } catch (e: Exception) {
                            val error = Left(e)
                            emit(error)
                            null
                        }
                    }
                    response.sentences.forEach {
                        if (!seen.contains(it.id)) {
                            seen.add(it.id)
                            emit(Right(it.toExample(langFrom)))
                        }
                    }
                    hasNextPage = response.sentences.size == LIMIT
                    offset += LIMIT
                }
            }
        }
    }

    private fun LeipzigSentence.toExample(lang: Lang) = LexicalItemDetail.Example(
        translationsSet = TranslationsSet(
            original = Sentence(
                text = this.sentence,
                lang = lang,
                source = this@WortschatzLeipzigLexicalItemDetailsSource.source,
            ),
            translations = emptyList(),
            translationsQualities = emptyList()
        ),
        source = this@WortschatzLeipzigLexicalItemDetailsSource.source,
    )

    private companion object {
        const val LIMIT = 10
    }
}

private fun encodePathSegment(value: String): String =
    URLEncoder.encode(value, "UTF-8").replace("+", "%20")

private fun textCorporaFor(lang: Lang): List<String> {
    // curl -X 'GET' \
    //  'https://api.wortschatz-leipzig.de/ws/corpora' \
    //  -H 'accept: application/json' | jq
    return when (lang) {
        Lang.RU -> listOf(
            "rus_news_2013_1M",
        )
        Lang.EN -> listOf(
            "eng_wikipedia_2012_1M",
            "eng_news_2013_3M",
            "eng_news_2012_3M",
        )
        Lang.DE -> listOf(
            "deu_wikipedia_2010_1M",
            "deu_news_2012_3M",
            "deu_news_2012_1M",
            "deu_news_2010_100K",
            "deu_news_2008_100K",
        )
        Lang.FR -> listOf(
            "fra_news_2011_3M",
        )
    }
}

private const val TAG = "WortschatzLeipzigLexicalItemDetailsSource"
