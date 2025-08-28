package blazern.langample.data.lexical_item_details_source.panlex

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.langample.graphql.LangampleApolloClientHolder
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.domain.model.DataSource.PANLEX
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_BASIC
import blazern.langample.graphql.model.LexicalItemsFromPanLexQuery
import blazern.langample.utils.FlowIterator
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.json.BufferedSourceJsonReader
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.parseResponse
import com.apollographql.apollo.exception.DefaultApolloException
import com.benasher44.uuid.Uuid
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PanLexLexicalItemDetailsSourceTest {
    private val apolloClient = mockk<ApolloClient>()
    private val holder = mockk<LangampleApolloClientHolder>()
    private val source = PanLexLexicalItemDetailsSource(
        holder,
        LexicalItemDetailsSourceCacher.NOOP,
    )

    init {
        every { holder.client } returns apolloClient
    }

    private fun op(query: String = "Haus", from: String = "deu", to: String = "eng") =
        LexicalItemsFromPanLexQuery(query, from, to)

    private fun parse(op: LexicalItemsFromPanLexQuery, json: String):
            ApolloResponse<LexicalItemsFromPanLexQuery.Data> {
        val reader: JsonReader = BufferedSourceJsonReader(Buffer().writeUtf8(json))
        return op.parseResponse(reader)
    }

    private fun successResponse(): ApolloResponse<LexicalItemsFromPanLexQuery.Data> {
        val json = """
            {
              "data": {
                "panlex": [
                  {
                    "__typename": "WordTranslations",
                    "source": "panlex",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "Haus",
                        "langIso3": "deu",
                        "source": "panlex"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "house",
                          "langIso3": "eng",
                          "source": "panlex"
                        },
                        {
                          "__typename": "Sentence",
                          "text": "building",
                          "langIso3": "eng",
                          "source": "panlex"
                        }
                      ],
                      "translationsQualities": [5]
                    }
                  },
                  {
                    "__typename": "Synonyms",
                    "source": "panlex",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "Haus",
                        "langIso3": "deu",
                        "source": "panlex"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "Gebäude",
                          "langIso3": "deu",
                          "source": "panlex"
                        },
                        {
                          "__typename": "Sentence",
                          "text": "Bauwerk",
                          "langIso3": "deu",
                          "source": "panlex"
                        }
                      ],
                      "translationsQualities": [2, 3]
                    }
                  }
                ]
              }
            }
        """.trimIndent()
        return parse(op(), json)
    }

    private fun networkErrorResponse(): ApolloResponse<LexicalItemsFromPanLexQuery.Data> {
        return ApolloResponse.Builder<LexicalItemsFromPanLexQuery.Data>(op(), Uuid.randomUUID())
            .exception(DefaultApolloException("no network"))
            .build()
    }

    private fun graphqlErrorsResponse(): ApolloResponse<LexicalItemsFromPanLexQuery.Data> {
        val json = """
            {
              "errors": [ { "message": "malformed response" } ],
              "data": null
            }
        """.trimIndent()
        return parse(op(), json)
    }

    @Test
    fun `source id and supported types`() = runBlocking {
        assertEquals(PANLEX, source.source)
        assertEquals(
            listOf(
                LexicalItemDetail.Type.WORD_TRANSLATIONS,
                LexicalItemDetail.Type.SYNONYMS,
            ),
            source.types
        )
    }

    @Test
    fun `good scenario`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromPanLexQuery>()).execute() } returns successResponse()

        val details = source.request("Haus", Lang.DE, Lang.EN)
            .take(10)
            .toList()
            .map { it.getOrElse { throw it } }

        val translations = details.filterIsInstance<LexicalItemDetail.WordTranslations>().single()
        assertEquals(
            LexicalItemDetail.WordTranslations(
                TranslationsSet(
                    original = Sentence("Haus", Lang.DE, PANLEX),
                    translations = listOf(
                        Sentence("house", Lang.EN, PANLEX),
                        Sentence("building", Lang.EN, PANLEX),
                    ),
                    // The first quality is specified in the backend response, but the second is not
                    translationsQualities = listOf(5, QUALITY_BASIC)
                ),
                PANLEX
            ),
            translations
        )

        val synonyms = details.filterIsInstance<LexicalItemDetail.Synonyms>().single()
        assertEquals(
            LexicalItemDetail.Synonyms(
                TranslationsSet(
                    Sentence("Haus", Lang.DE, PANLEX),
                    listOf(
                        Sentence("Gebäude", Lang.DE, PANLEX),
                        Sentence("Bauwerk", Lang.DE, PANLEX),
                    ),
                    translationsQualities = listOf(2, 3)
                ),
                PANLEX
            ),
            synonyms
        )
    }

    @Test
    fun `first IO error then good scenario`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromPanLexQuery>()).execute() } returnsMany listOf(
            networkErrorResponse(),
            successResponse(),
        )

        val flow = source.request("Haus", Lang.DE, Lang.EN)
        val iter = FlowIterator(flow)
        assertTrue(iter.next() is Left)   // network error
        assertTrue(iter.next() is Right)  // success afterwards
        iter.close()
    }

    @Test
    fun `graphql errors`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromPanLexQuery>()).execute() } returns graphqlErrorsResponse()

        val flow = source.request("Haus", Lang.DE, Lang.EN)
        val iter = FlowIterator(flow)
        assertTrue(iter.next() is Left)
        iter.close()
    }
}
