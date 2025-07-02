package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Test

import org.junit.Assert.*

class ChatGPTLexicalItemDetailsSourceTest {
    private val chatGpt = mockk<ChatGPTClient>()
    private val source  = ChatGPTLexicalItemDetailsSource(chatGpt)

    @Test
    fun `good scenario`() = runBlocking {
        val json = """
            {
              "forms": "der Hund, -e",
              "explanation": "Der Hund ist ein Haustier.",
              "examples": [
                "Dog|Hund",
                "My dog|Mein Hund"
              ]
            }
        """.trimIndent()
        coEvery { chatGpt.request(any()) } returns Right(json)

        val futureDetails = source.request("dog", Lang.EN, Lang.DE)

        val explanation = futureDetails.single { it.type == LexicalItemDetail.Type.EXPLANATION }
            .details.single().getOrNull()!! as LexicalItemDetail.Explanation
        assertEquals(
            LexicalItemDetail.Explanation("Der Hund ist ein Haustier.", DataSource.CHATGPT),
            explanation,
        )

        val forms = futureDetails.single { it.type == LexicalItemDetail.Type.FORMS }
            .details.single().getOrNull()!! as LexicalItemDetail.Forms
        assertEquals(
            LexicalItemDetail.Forms("der Hund, -e", DataSource.CHATGPT),
            forms,
        )


        val examples = futureDetails.single { it.type == LexicalItemDetail.Type.EXAMPLE }
            .details.map { it.getOrNull()!! as LexicalItemDetail.Example }
            .toList()
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
    fun `IO error scenario`() = runBlocking {
        val error = IOException("no network")
        coEvery { chatGpt.request(any()) } returns Left(error)

        val futures = source.request("dog", Lang.EN, Lang.DE)

        futures.forEach { future ->
            val emissions = future.details.toList()
            assertEquals(1, emissions.size)
            assertTrue(emissions.first() is Left)
            assertEquals(error, emissions.first().leftOrNull()!!)
        }
    }

    @Test
    fun `malformed JSON`() = runBlocking {
        coEvery { chatGpt.request(any()) } returns Right("{ error }")

        val futures = source.request("dog", Lang.EN, Lang.DE)

        futures.forEach { future ->
            val emissions = future.details.toList()
            assertEquals(1, emissions.size)
            assertTrue(emissions.first() is Left)
        }
    }

    @Test
    fun `upstream executes once for multiple subflows`() = runBlocking {
        val json = """{ "forms":"x", "explanation":"x", "examples": [] }"""
        coEvery { chatGpt.request(any()) } returns Right(json)

        val futures = source.request("dog", Lang.EN, Lang.DE)
        // Collect all flows, they should share the single result
        futures.forEach {
            it.details.toList()
        }

        coVerify(exactly = 1) { chatGpt.request(any()) }
    }
}
