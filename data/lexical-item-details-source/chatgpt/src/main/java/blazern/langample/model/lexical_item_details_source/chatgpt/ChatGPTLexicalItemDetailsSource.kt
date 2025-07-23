package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.WordTranslations
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.json.Json

class ChatGPTLexicalItemDetailsSource(
    private val chatGPTClient: ChatGPTClient,
) : LexicalItemDetailsSource {
    override val source = DataSource.CHATGPT
    override val types = listOf(
        LexicalItemDetail.Type.FORMS,
        LexicalItemDetail.Type.WORD_TRANSLATIONS,
        LexicalItemDetail.Type.SYNONYMS,
        LexicalItemDetail.Type.EXPLANATION,
        LexicalItemDetail.Type.EXAMPLE,
    )

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): LexicalItemDetailsFlow {
        return flow {
            while (true) {
                val result = queryChatGPT(query, langFrom, langTo)
                result.fold(
                    { emit(Left(it)) },
                    {
                        emit(Right(LexicalItemDetail.Forms(it.forms, source)))
                        emit(Right(WordTranslations(
                            TranslationsSet(
                                Sentence(query, langFrom, source),
                                it.translations.map {
                                    Sentence(it, langTo, source)
                                }
                            ),
                            source,
                        )))
                        emit(Right(LexicalItemDetail.Synonyms(
                            TranslationsSet(
                                Sentence(query, langFrom, source),
                                it.synonyms.map {
                                    Sentence(it, langFrom, source)
                                }
                            ),
                            source,
                        )))
                        emit(Right(LexicalItemDetail.Explanation(it.explanation, source)))
                        it.convertExamples(langFrom, langTo).forEach {
                            emit(Right(LexicalItemDetail.Example(it, source)))
                        }
                        return@flow
                    }
                )
            }
        }
    }

    private suspend fun queryChatGPT(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): Either<Exception, ChatGPTResponse> {
        val chatGptQuery = generateChatGPTQuery(
            query,
            langFrom,
            langTo,
        )
        val responseText = chatGPTClient.request(chatGptQuery).getOrElse {
            return Left(it)
        }

        val response: ChatGPTResponse = try {
            Json.decodeFromString(responseText)
        } catch (e: SerializationException) {
            return Left(e)
        } catch (e: JsonConvertException) {
            return Left(e)
        }
        return Right(response)
    }

    private companion object {
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

        fun generateChatGPTQuery(
            query: String,
            langFrom: Lang,
            langTo: Lang,
        ): String {
            return """
you are called from a language learning app, your goal is to
generate a reply in next format:
{
    "forms": "<FORMS>",
    "translations": ["<TRANSLATION>", "<TRANSLATION>", "<TRANSLATION>"],
    "synonyms": ["<SYNONYM>", "<SYNONYM>", "<SYNONYM>"],
    "explanation": "<EXPLANATION_SOURCE_LANG>",
    "examples": [
        "<EXAMPLE>",
        "<EXAMPLE>",
        "<EXAMPLE>",
        "<EXAMPLE>",
        "<EXAMPLE>"
    ]
}
The reply must explain next word: $query
Source lang: ${langFrom.iso3}
Target lang: ${langTo.iso3}
Placeholders
<FORMS>: $formsExplanation
<TRANSLATION>: a translation into ${langTo.iso3}
<SYNONYM>: a synonym in ${langFrom.iso3}
<EXPLANATION_SOURCE_LANG>: short (2-3 sentences) explanation of the word, lang: ${langTo.iso3}
<EXAMPLE>: example sentence ${langFrom.iso3} | example sentence ${langTo.iso3}
Where "|" is a required delimiter, example sentences must be short.
Translations and synonyms may contain 1-6 entries.
            """.trimIndent()
        }
    }
}

@Serializable
private class ChatGPTResponse(
    val forms: String,
    val translations: List<String>,
    val synonyms: List<String>,
    val explanation: String,
    val examples: List<String>,
)

private fun ChatGPTResponse.convertExamples(
    langFrom: Lang,
    langTo: Lang,
): List<TranslationsSet> {
    return examples.map {
        TranslationsSet(
            original = Sentence(
                it.split("|").first().trim(),
                langTo,
                DataSource.CHATGPT,
            ),
            translations = listOf(
                Sentence(
                    it.split("|").last().trim(),
                    langFrom,
                    DataSource.CHATGPT,
                )
            )
        )
    }
}
