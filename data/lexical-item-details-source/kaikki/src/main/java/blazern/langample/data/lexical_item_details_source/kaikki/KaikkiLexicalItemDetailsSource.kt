package blazern.langample.data.lexical_item_details_source.kaikki

import arrow.core.Either
import arrow.core.Either.Left
import blazern.langample.data.kaikki.KaikkiClient
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.flow

class KaikkiLexicalItemDetailsSource(
    private val kaikkiClient: KaikkiClient,
    private val settings: SettingsRepository,
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
    ): LexicalItemDetailsFlow {
        return flow {
            while (true) {
                val entries = kaikkiClient.search(query, langFrom)
                entries.fold(
                    { emit(Left(it)) },
                    {
                        for (entry in it) {
                            // TODO: do something if no acceptable forms were found... show all of them?
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
                                emit(Either.Right(LexicalItemDetail.Forms(
                                    formsStrs.joinToString(", "),
                                    source,
                                )))
                            }
                            if (entry.senses.isNotEmpty()) {
                                for (sense in entry.senses) {
                                    for (gloss in sense.glosses) {
                                        emit(Either.Right(Explanation(gloss, source)))
                                    }
                                    for (example in sense.examples) {
                                        emit(Either.Right(LexicalItemDetail.Example(
                                            TranslationsSet(
                                                original = Sentence(example.text, langTo, source),
                                                translations = emptyList(),
                                            ),
                                            source,
                                        )))
                                    }
                                }
                            }
                        }
                        return@flow
                    }
                )
            }
        }
    }
}
