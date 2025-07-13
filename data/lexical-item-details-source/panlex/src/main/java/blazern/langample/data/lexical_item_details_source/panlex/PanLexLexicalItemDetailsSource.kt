package blazern.langample.data.lexical_item_details_source.panlex

import arrow.core.Either
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.panlex.PanLexClient
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Synonyms
import blazern.langample.domain.model.LexicalItemDetail.WordTranslations
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import kotlinx.coroutines.flow.flow

class PanLexLexicalItemDetailsSource(
    private val panLexClient: PanLexClient,
) : LexicalItemDetailsSource {
    override val source = DataSource.PANLEX
    override val types = listOf(
        LexicalItemDetail.Type.WORD_TRANSLATIONS,
        LexicalItemDetail.Type.SYNONYMS,
    )

    override fun request(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): LexicalItemDetailsFlow {
        return flow {
            while (true) {
                val panlexResult = panLexClient.search(query, langFrom, listOf(langTo))
                panlexResult.fold(
                    { emit(Either.Left(it)) },
                    {
                        if (it.translations.isNotEmpty()) {
                            val translations = TranslationsSet(
                                original = Sentence(query, langFrom, source),
                                translations = it.translations.map {
                                    Sentence(it.word, it.lang, source)
                                },
                            )
                            emit(Either.Right(WordTranslations(translations, source)))
                        }
                        if (it.synonyms.isNotEmpty()) {
                            val synonyms = TranslationsSet(
                                original = Sentence(query, langFrom, source),
                                translations = it.synonyms.map {
                                    Sentence(it.word, it.lang, source)
                                },
                            )
                            emit(Either.Right(Synonyms(synonyms, source)))
                        }
                        return@flow
                    }
                )
            }
        }
    }
}
