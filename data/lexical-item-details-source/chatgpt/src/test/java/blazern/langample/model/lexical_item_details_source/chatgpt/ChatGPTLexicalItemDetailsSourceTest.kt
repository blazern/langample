package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.langample.graphql.LangampleApolloClientHolder
import blazern.langample.domain.model.DataSource.CHATGPT
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_MAX
import blazern.langample.graphql.model.LexicalItemsFromLLMQuery
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

class ChatGPTLexicalItemDetailsSourceTest {
    private val apolloClient = mockk<ApolloClient>()
    private val holder = mockk<LangampleApolloClientHolder>()
    private val source  = ChatGPTLexicalItemDetailsSource(holder)

    init {
        every { holder.client } returns apolloClient
    }

    private fun op(query: String = "Hund", from: String = "deu", to: String = "eng") =
        LexicalItemsFromLLMQuery(query, from, to)

    private fun parse(op: LexicalItemsFromLLMQuery, json: String):
            ApolloResponse<LexicalItemsFromLLMQuery.Data> {
        val reader: JsonReader = BufferedSourceJsonReader(Buffer().writeUtf8(json))
        return op.parseResponse(reader)
    }

    private fun successResponse(): ApolloResponse<LexicalItemsFromLLMQuery.Data> {
        val json = """
            {
              "data": {
                "llm": [
                  { "__typename": "Forms", "source": "chatgpt", "text": "der Hund, -e" },
                  {
                    "__typename": "Explanation",
                    "source": "chatgpt",
                    "text": "Der Hund ist ein Haustier."
                  },
                  {
                    "__typename": "WordTranslations",
                    "source": "chatgpt",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "Hund",
                        "langIso3": "deu",
                        "source": "chatgpt"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "dog",
                          "langIso3": "eng",
                          "source": "chatgpt"
                        },
                        {
                          "__typename": "Sentence",
                          "text": "hound",
                          "langIso3": "eng",
                          "source": "chatgpt"
                        }
                      ]
                    }
                  },
                  {
                    "__typename": "Synonyms",
                    "source": "chatgpt",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "Hund",
                        "langIso3": "deu",
                        "source": "chatgpt"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "Hündin",
                          "langIso3": "deu",
                          "source": "chatgpt"
                        },
                        {
                          "__typename": "Sentence",
                          "text": "Köter",
                          "langIso3": "deu",
                          "source": "chatgpt"
                        }
                      ]
                    }
                  },
                  {
                    "__typename": "Example",
                    "source": "chatgpt",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "Dog",
                        "langIso3": "eng",
                        "source": "chatgpt"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "Hund",
                          "langIso3": "deu",
                          "source": "chatgpt"
                        }
                      ]
                    }
                  },
                  {
                    "__typename": "Example",
                    "source": "chatgpt",
                    "translationsSet": {
                      "__typename": "TranslationsSet",
                      "original": {
                        "__typename": "Sentence",
                        "text": "My dog",
                        "langIso3": "eng",
                        "source": "chatgpt"
                      },
                      "translations": [
                        {
                          "__typename": "Sentence",
                          "text": "Mein Hund",
                          "langIso3": "deu",
                          "source": "chatgpt"
                        }
                      ]
                    }
                  }
                ]
              }
            }
        """.trimIndent()
        return parse(op(), json)
    }

    private fun networkErrorResponse(): ApolloResponse<LexicalItemsFromLLMQuery.Data> {
        return ApolloResponse.Builder<LexicalItemsFromLLMQuery.Data>(op(), Uuid.randomUUID())
            .exception(DefaultApolloException("no network"))
            .build()
    }

    private fun graphqlErrorsResponse(): ApolloResponse<LexicalItemsFromLLMQuery.Data> {
        val json = """
            {
              "errors": [ { "message": "malformed response" } ],
              "data": null
            }
        """.trimIndent()
        return parse(op(), json)
    }

    @Test
    fun `good scenario`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromLLMQuery>()).execute() } returns successResponse()

        val details = source.request("Hund", Lang.DE, Lang.EN)
            .take(20)
            .toList()
            .map { it.getOrElse { throw it } }

        val explanation = details
            .filterIsInstance<LexicalItemDetail.Explanation>()
            .single()
        assertEquals(
            LexicalItemDetail.Explanation("Der Hund ist ein Haustier.", CHATGPT),
            explanation,
        )

        val forms = details
            .filterIsInstance<LexicalItemDetail.Forms>()
            .single()
        assertEquals(
            LexicalItemDetail.Forms("der Hund, -e", CHATGPT),
            forms,
        )

        val translations = details
            .filterIsInstance<LexicalItemDetail.WordTranslations>()
            .single()
        assertEquals(
            LexicalItemDetail.WordTranslations(
                TranslationsSet(
                    Sentence("Hund", Lang.DE, CHATGPT),
                    listOf(
                        Sentence("dog", Lang.EN, CHATGPT),
                        Sentence("hound", Lang.EN, CHATGPT),
                    ),
                    listOf(QUALITY_MAX, QUALITY_MAX),
                ),
                CHATGPT,
            ),
            translations,
        )

        val synonyms = details
            .filterIsInstance<LexicalItemDetail.Synonyms>()
            .single()
        assertEquals(
            LexicalItemDetail.Synonyms(
                TranslationsSet(
                    Sentence("Hund", Lang.DE, CHATGPT),
                    listOf(
                        Sentence("Hündin", Lang.DE, CHATGPT),
                        Sentence("Köter", Lang.DE, CHATGPT),
                    ),
                    listOf(QUALITY_MAX, QUALITY_MAX),
                ),
                CHATGPT,
            ),
            synonyms,
        )

        val examples = details
            .filterIsInstance<LexicalItemDetail.Example>()
        val expectedSets = listOf(
            TranslationsSet(
                Sentence("Dog", Lang.EN, CHATGPT),
                listOf(Sentence("Hund", Lang.DE, CHATGPT)),
                listOf(QUALITY_MAX),
            ),
            TranslationsSet(
                Sentence("My dog", Lang.EN, CHATGPT),
                listOf(Sentence("Mein Hund", Lang.DE, CHATGPT)),
                listOf(QUALITY_MAX),
            ),
        )
        val expectedExamples = expectedSets.map {
            LexicalItemDetail.Example(it, CHATGPT)
        }

        assertEquals(expectedExamples, examples)
    }

    @Test
    fun `first IO error then good scenario`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromLLMQuery>()).execute() } returnsMany listOf(
            networkErrorResponse(),
            successResponse(),
        )

        val flow = source.request("dog", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow, this)
        assertTrue(iter.next() is Left)
        assertTrue(iter.next() is Right)
        iter.close()
    }

    @Test
    fun `graphql errors`() = runBlocking {
        coEvery { apolloClient.query(any<LexicalItemsFromLLMQuery>()).execute() } returns graphqlErrorsResponse()

        val flow = source.request("dog", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow, this)
        assertTrue(iter.next() is Left)
        iter.close()
    }
}
