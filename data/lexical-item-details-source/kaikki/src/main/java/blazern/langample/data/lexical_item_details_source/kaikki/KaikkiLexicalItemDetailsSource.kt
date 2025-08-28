package blazern.langample.data.lexical_item_details_source.kaikki

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.kaikki.KaikkiClient
import blazern.langample.data.kaikki.model.Entry
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.cache.LexicalItemDetailsSourceCacher
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class KaikkiLexicalItemDetailsSource(
    private val kaikkiClient: KaikkiClient,
    private val settings: SettingsRepository,
    private val cacher: LexicalItemDetailsSourceCacher,
) : LexicalItemDetailsSource {
    override val source = DataSource.KAIKKI
    override val types = listOf(
        LexicalItemDetail.Type.FORMS,
        LexicalItemDetail.Type.EXPLANATION,
        LexicalItemDetail.Type.EXAMPLE,
    )

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow = cacher.retrieveOrExecute(source, query, langFrom, langTo) {
        requestImpl(query, langFrom, langTo)
    }

    private fun requestImpl(
        query: String,
        langFrom: Lang,
        langTo: Lang,
        depth: Int = 0,
    ): Flow<Either<Exception, LexicalItemDetail>> {
        return flow {
            var entries: List<Entry>? = null
            do {
                entries = kaikkiClient.search(query, langFrom).fold(
                    { emit(Left(it)); null },
                    { it }
                )
            } while (entries == null)

            for (entry in entries) {
                val formsOf = entry.senses.map { it.formOf }.flatten()
                val purelyWordForm = formsOf.size == entry.senses.size
                if (!purelyWordForm) {
                    val details = toDetails(entry, langTo)
                    details.forEach {
                        emit(Right(it))
                    }
                }
                if (depth == 0) {
                    formsOf.forEach {
                        emitAll(requestImpl(it.word, langFrom, langTo, depth + 1))
                    }
                }
            }
        }
    }

    private suspend fun toDetails(entry: Entry, langTo: Lang): List<LexicalItemDetail> {
        var result = mutableListOf<LexicalItemDetail>()
        val acceptableFormsTags = settings.getTatoebaAcceptableTagsSets()
        val formsStrs = mutableListOf<String>()
        for (forms in entry.forms) {
            if (acceptableFormsTags.contains(forms.tags?.toSet())) {
                if (forms.form.isNotBlank()) {
                    formsStrs += forms.form
                }
            }
        }
        if (formsStrs.isNotEmpty()) {
            result += LexicalItemDetail.Forms(
                formsStrs.joinToString(", "),
                source,
            )
        }
        if (entry.senses.isNotEmpty()) {
            for (sense in entry.senses) {
                for (gloss in sense.glosses) {
                    result += Explanation(gloss, source)
                }
                for (example in sense.examples) {
                    result += LexicalItemDetail.Example(
                        TranslationsSet(
                            original = Sentence(example.text, langTo, source),
                            translations = emptyList(),
                            translationsQualities = emptyList(),
                        ),
                        source,
                    )
                }
            }
        }
        return result
    }
}
