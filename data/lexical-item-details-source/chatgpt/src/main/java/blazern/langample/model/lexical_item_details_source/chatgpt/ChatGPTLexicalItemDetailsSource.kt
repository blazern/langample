package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.data.lexical_item_details_source.api.FutureLexicalItemDetails
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class ChatGPTLexicalItemDetailsSource(
    private val chatGPTClient: ChatGPTClient,
) : LexicalItemDetailsSource {
    override val source = DataSource.CHATGPT

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): List<FutureLexicalItemDetails> {
        val cache = AtomicReference<Either<Exception, ChatGPTResponse>?>(null)
        val guard = Mutex()
        suspend fun obtain(): Either<Exception, ChatGPTResponse> {
            return guard.withLock {
                cache.load() ?: run {
                    val result = queryChatGPT(query, langFrom, langTo)
                    cache.store(result)
                    result
                }
            }
        }

        val explanation = flow {
            emit(obtain().map { LexicalItemDetail.Explanation(it.explanation, source) })
        }
        val forms = flow {
            emit(obtain().map { LexicalItemDetail.Forms(it.forms, source) })
        }
        val examples: Flow<Either<Exception, LexicalItemDetail>> = flow {
            obtain().map { it.convertExamples(langFrom, langTo) }.fold(
                { emit(Left(it)) },
                {
                    it.forEach {
                        emit(Right(LexicalItemDetail.Example(it, source)))
                    }
                }
            )
        }
        return listOf(
            FutureLexicalItemDetails(
                explanation,
                LexicalItemDetail.Type.EXPLANATION,
                source,
            ),
            FutureLexicalItemDetails(
                forms,
                LexicalItemDetail.Type.FORMS,
                source,
            ),
            FutureLexicalItemDetails(
                examples,
                LexicalItemDetail.Type.EXAMPLE,
                source,
            ),
        )
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
        }
    }
}

@Serializable
private class ChatGPTResponse(
    val forms: String,
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
}
