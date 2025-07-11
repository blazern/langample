package blazern.langample.data.kaikki

import arrow.core.Either
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.domain.model.Lang
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class KaikkiClient(
    private val ktorClientHolder: KtorClientHolder,
    private val cpuDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun search(
        query: String,
        langFrom: Lang,
    ): Either<Exception, List<Entry>> {
        val url = "https://kaikki.org/" + subwiktionaryOf(langFrom) + "/meaning/" + wordPagePostfix(query)
        return withContext(cpuDispatcher) {
            try {
                val entries = ktorClientHolder.client.get(url)
                    .bodyAsText()
                    .lineSequence()
                    .filter { it.isNotBlank() }
                    .map { json.decodeFromString<Entry>(it) }
                    .toList()
                Either.Right(entries)
            } catch (e: IOException) {
                Either.Left(e)
            } catch (e: SerializationException) {
                Either.Left(e)
            }
        }
    }
}

private fun subwiktionaryOf(lang: Lang): String {
    return when (lang) {
        Lang.RU -> "ruwiktionary/Русский"
        Lang.EN -> "dictionary/English"
        Lang.DE -> "dewiktionary/Deutsch"
    }
}

private fun wordPagePostfix(word: String): String {
    return "${word[0]}/${word.substring(0, 2)}/${word}.jsonl"
}
