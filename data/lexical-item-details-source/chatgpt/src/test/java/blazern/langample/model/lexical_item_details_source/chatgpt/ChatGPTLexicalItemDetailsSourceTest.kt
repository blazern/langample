package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.DataSource.CHATGPT
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatGPTLexicalItemDetailsSourceTest {
    private val chatGpt = mockk<ChatGPTClient>()
    private val source  = ChatGPTLexicalItemDetailsSource(chatGpt)

    private val chatGptJson = """
            {
              "forms": "der Hund, -e",
              "translations": ["dog", "hound"],
              "synonyms": ["Hündin", "Köter"],
              "explanation": "Der Hund ist ein Haustier.",
              "examples": [
                "Dog|Hund",
                "My dog|Mein Hund"
              ]
            }
        """.trimIndent()

    @Test
    fun `good scenario`() = runBlocking {
        coEvery { chatGpt.request(any()) } returns Right(chatGptJson)

        val details = source.request("Hund", Lang.DE, Lang.EN)
            .take(20) // We're not expecting a lot of results
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
                    )),
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
                    )),
                CHATGPT,
            ),
            synonyms,
        )

        val examples = details
            .filterIsInstance<LexicalItemDetail.Example>()
        val expectedSets = listOf(
            TranslationsSet(
                Sentence("Dog", Lang.EN, CHATGPT),
                listOf(Sentence("Hund", Lang.DE, CHATGPT))
            ),
            TranslationsSet(
                Sentence("My dog", Lang.EN, CHATGPT),
                listOf(Sentence("Mein Hund", Lang.DE, CHATGPT))
            ),
        )
        val expectedExamples = expectedSets.map {
            LexicalItemDetail.Example(it, CHATGPT)
        }

        assertEquals(expectedExamples, examples)
    }

    @Test
    fun `first IO error then good scenario`() = runBlocking {
        // Bad
        coEvery { chatGpt.request(any()) } returns Left(IOException("no network"))
        val flow = source.request("dog", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow, this)
        assertTrue(iter.next() is Left)

        coEvery { chatGpt.request(any()) } returns Right(chatGptJson)
        assertTrue(iter.next() is Right)
        iter.close()
    }

    @Test
    fun `malformed JSON`() = runBlocking {
        coEvery { chatGpt.request(any()) } returns Right("{ error }")

        val flow = source.request("dog", Lang.EN, Lang.DE)
        val iter = FlowIterator(flow, this)
        assertTrue(iter.next() is Left)
        iter.close()
    }
}
