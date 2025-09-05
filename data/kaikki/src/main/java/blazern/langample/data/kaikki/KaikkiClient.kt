package blazern.langample.data.kaikki

import arrow.core.Either
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.domain.model.Lang
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class KaikkiClient(
    private val ktorClientHolder: KtorClientHolder,
    private val cpuDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun search(
        query: String,
        langFrom: Lang,
    ): Either<Exception, List<Entry>> {
        val queryToUrl = { query: String ->
            "https://kaikki.org/" + subwiktionaryOf(langFrom) + "/meaning/" + wordPagePostfix(query)
        }
        return withContext(cpuDispatcher) {
            try {
                var result = ktorClientHolder.client.get(queryToUrl(query))
                if (result.status == HttpStatusCode.NotFound) {
                    val updatedQuery = if (query.firstOrNull()?.isUpperCase() == true) {
                        query.replaceFirstChar { it.lowercase() }
                    } else {
                        query.replaceFirstChar { it.uppercase() }
                    }
                    result = ktorClientHolder.client.get(queryToUrl(updatedQuery))
                }
                if (result.status == HttpStatusCode.NotFound) {
                    return@withContext Either.Right(emptyList())
                }
                val entries = result
                    .bodyAsText()
                    .lineSequence()
                    .filter { it.isNotBlank() }
                    .map { json.decodeFromString<Entry>(it) }
                    .toList()
                Either.Right(entries)
            } catch (e: Exception) {
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
        Lang.FR -> "frwiktionary/Français"
    }
}

private fun wordPagePostfix(word: String): String {
    return "${word[0]}/${word.substring(0, 2)}/${word}.jsonl"
}
