package blazern.langample.model.lexical_item_details_source.chatgpt

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import blazern.langample.data.langample.graphql.LangampleApolloClientHolder
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource.Item
import blazern.langample.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.langample.domain.error.Err
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import blazern.langample.graphql.model.LexicalItemsFromLLMQuery
import blazern.langample.graphql.model.fragment.SentenceFields
import blazern.langample.graphql.model.fragment.TranslationsSetFields
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ChatGPTLexicalItemDetailsSource(
    private val apolloClientHolder: LangampleApolloClientHolder,
    private val cacher: LexicalItemDetailsSourceCacher,
) : LexicalItemDetailsSource {
    private val apollo: ApolloClient
        get() = apolloClientHolder.client

    override val source = DataSource.CHATGPT
    override val types = setOf(
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
    ): Flow<Item> = cacher.retrieveOrExecute(source, query, langFrom, langTo) {
        requestImpl(query, langFrom, langTo)
    }

    private fun requestImpl(
        query: String,
        langFrom: Lang,
        langTo: Lang
    ): Flow<Item> = flow {
        while (true) {
            val result = apolloRequest(query, langFrom, langTo)
            result.fold(
                { emit(Item.Failure(Err.from(it))) },
                {
                    emit(Item.Page(
                        details = it,
                        nextPageTypes = types,
                    ))
                    return@flow
                }
            )
        }
    }

    private suspend fun apolloRequest(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ): Either<Exception, List<LexicalItemDetail>> {
        val result = apollo.query(
            LexicalItemsFromLLMQuery(
                query,
                langFromIso3 = langFrom.iso3,
                langToIso3 = langTo.iso3,
            )
        ).execute()

        result.exception?.let {
            return Left(it)
        }

        if (!result.errors.isNullOrEmpty()) {
            val msg = result.errors!!.joinToString("; ") { it.message }
            return Left(IOException(msg))
        }

        val data = result.data ?: return Right(emptyList())

        val items = data.llm.mapNotNull {
            it.toDomain().getOrElse { return Left(it) }
        }
        return Right(items)
    }
}

private fun LexicalItemsFromLLMQuery.Llm.toDomain(): Either<IllegalArgumentException, LexicalItemDetail?> {
    onForms?.let {
        return Right(LexicalItemDetail.Forms(
            value = LexicalItemDetail.Forms.Value.Text(it.text),
            source = mapSource(it.source)
        ))
    }
    onExplanation?.let {
        return Right(LexicalItemDetail.Explanation(
            text = it.text,
            source = mapSource(it.source)
        ))
    }
    onWordTranslations?.let {
        val ts = it.translationsSet.translationsSetFields
        return Right(LexicalItemDetail.WordTranslations(
            translationsSet = ts.toDomain().getOrElse { return Left(it) },
            source = mapSource(it.source)
        ))
    }
    onSynonyms?.let {
        val ts = it.translationsSet.translationsSetFields
        return Right(LexicalItemDetail.Synonyms(
            translationsSet = ts.toDomain().getOrElse { return Left(it) },
            source = mapSource(it.source)
        ))
    }
    onExample?.let {
        val ts = it.translationsSet.translationsSetFields
        return Right(LexicalItemDetail.Example(
            translationsSet = ts.toDomain().getOrElse { return Left(it) },
            source = mapSource(it.source)
        ))
    }
    // New field in a newer version of the backend was added apparently
    return Right(null)
}

private fun TranslationsSetFields.toDomain(): Either<IllegalArgumentException, TranslationsSet> {
    return Right(TranslationsSet(
        original = original.toDomain()
            .getOrElse { return Left(it) },
        translations = translations.map {
            it.toDomain().getOrElse { return Left(it) }
        },
        translationsQualities = translations.map { TranslationsSet.QUALITY_MAX },
    ))
}

private fun TranslationsSetFields.Original.toDomain():
        Either<IllegalArgumentException, Sentence> = this.sentenceFields.toDomain()

private fun TranslationsSetFields.Translation.toDomain():
        Either<IllegalArgumentException, Sentence> = this.sentenceFields.toDomain()

private fun SentenceFields.toDomain(): Either<IllegalArgumentException, Sentence> {
    val lang = Lang.fromIso3(langIso3)
        ?: return Left(IllegalArgumentException("Lang $langIso3 not supported"))
    return Right(Sentence(
        text = text,
        lang = lang,
        source = mapSource(source),
    ))
}

@Suppress("UnusedParameter")
private fun mapSource(remoteSource: String) = DataSource.CHATGPT
