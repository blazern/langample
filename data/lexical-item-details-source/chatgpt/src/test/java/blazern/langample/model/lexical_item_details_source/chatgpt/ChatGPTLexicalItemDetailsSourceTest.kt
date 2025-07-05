package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.utils.FlowIterator
import io.mockk.coEvery
import io.mockk.mockk
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

        val details = source.request("dog", Lang.EN, Lang.DE)
            .toList()
            .map { it.getOrNull()!! }

        val explanation = details
            .filterIsInstance<LexicalItemDetail.Explanation>()
            .single()
        assertEquals(
            LexicalItemDetail.Explanation("Der Hund ist ein Haustier.", DataSource.CHATGPT),
            explanation,
        )

        val forms = details
            .filterIsInstance<LexicalItemDetail.Forms>()
            .single()
        assertEquals(
            LexicalItemDetail.Forms("der Hund, -e", DataSource.CHATGPT),
            forms,
        )

        val examples = details
            .filterIsInstance<LexicalItemDetail.Example>()
        val expectedSets = listOf(
            TranslationsSet(
                Sentence("Dog", Lang.EN, DataSource.CHATGPT),
                listOf(Sentence("Hund", Lang.DE, DataSource.CHATGPT))
            ),
            TranslationsSet(
                Sentence("My dog", Lang.EN, DataSource.CHATGPT),
                listOf(Sentence("Mein Hund", Lang.DE, DataSource.CHATGPT))
            ),
        )
        val expectedExamples = expectedSets.map {
            LexicalItemDetail.Example(it, DataSource.CHATGPT)
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
