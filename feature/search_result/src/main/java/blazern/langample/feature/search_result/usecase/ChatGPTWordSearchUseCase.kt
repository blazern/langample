package blazern.langample.feature.search_result.usecase

import android.util.Log
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.feature.search_result.llm.LLMWordExplanation
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal class ChatGPTWordSearchUseCase(
    private val chatGPTClient: ChatGPTClient,
) {
    suspend fun invoke(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): Result<LLMWordExplanation> {
        val formsExplanation = """
                if noun: article, singular form, plural form changes, e.g.:
                der Hund, -e
                der Platz, -äe
                der Wurm, -(ü)e
                if verb: follow next examples:
                gehen, geht, ging, ist gegangen
                lieben, liebt, liebte, hat geliebt
                for others (adverb, adjective) make it as simple as possible
            """.trimIndent()

        val request = """
                you are called from a language learning app, your goal is to
                generate a reply in next format:
                {
                    "forms": "<FORMS>",
                    "explanation": "<EXPLANATION_SOURCE_LANG>",
                    "examples": [
                        "<EXAMPLE>",
                        "<EXAMPLE>",
                        "<EXAMPLE>",
                    ]
                }
                The reply must explain next word: $query
                Source lang: ${langFrom.iso2}
                Target lang: ${langTo.iso2}
                Placeholders
                <FORMS>: $formsExplanation
                <EXPLANATION_SOURCE_LANG>: short (2-3 sentences) explanation of the word in ${langTo.iso2}
                <EXAMPLE>: example sentence ${langFrom.iso2} | example sentence ${langTo.iso2}
                Where "|" is a required delimiter, example sentences must be short.
            """.trimIndent()

        val responseText = chatGPTClient.request(request)
            .getOrElse { return Result.failure(it) }

        val response: ChatGPTResponse = try {
             Json.decodeFromString(responseText)
        } catch (e: SerializationException) {
            Log.e(TAG, "Exception while parsing ChatGPT response: $responseText", e)
            return Result.failure(e)
        }

        val examples = response.examples.map {
            TranslationsSet(
                original = Sentence(
                    it.split("|").first(),
                    langFrom,
                    DataSource.CHATGPT,
                ),
                translations = listOf(
                    Sentence(
                        it.split("|").last(),
                        langTo,
                        DataSource.CHATGPT,
                    )
                )
            )
        }
        return Result.success(LLMWordExplanation(
            formsHtml = response.forms,
            explanation = response.explanation,
            examples = examples,
        ))
    }

    private companion object {
        val TAG = "ChatGPTWordSearchUseCase"
    }
}

@Serializable
private class ChatGPTResponse(
    val forms: String,
    val explanation: String,
    val examples: List<String>
)
