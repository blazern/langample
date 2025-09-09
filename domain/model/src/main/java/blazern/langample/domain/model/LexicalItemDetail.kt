package blazern.langample.domain.model

import androidx.annotation.StringRes
import blazern.langample.core.strings.R
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import blazern.langample.domain.model.LexicalItemDetail.Synonyms
import blazern.langample.domain.model.LexicalItemDetail.WordTranslations
import kotlin.reflect.KClass

sealed class LexicalItemDetail(
    val type: Type,
    open val source: DataSource,
) {
    data class Forms(
        val value: Value,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.FORMS, source) {
        sealed class Value {
            data class Text(val text: String) : Value()
            data class Detailed(val forms: List<WordForm>) : Value()
        }
    }

    data class WordTranslations(
        val translationsSet: TranslationsSet,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.WORD_TRANSLATIONS, source)

    data class Synonyms(
        val translationsSet: TranslationsSet,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.SYNONYMS, source)

    data class Explanation(
        val text: String,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.EXPLANATION, source)

    data class Example(
        val translationsSet: TranslationsSet,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.EXAMPLE, source)

    enum class Type {
        FORMS,
        WORD_TRANSLATIONS,
        SYNONYMS,
        EXPLANATION,
        EXAMPLE,
    }

    companion object
}

fun LexicalItemDetail.Type.toClass(): KClass<out LexicalItemDetail> {
    return when (this) {
        LexicalItemDetail.Type.FORMS -> Forms::class
        LexicalItemDetail.Type.WORD_TRANSLATIONS -> WordTranslations::class
        LexicalItemDetail.Type.SYNONYMS -> Synonyms::class
        LexicalItemDetail.Type.EXPLANATION -> Explanation::class
        LexicalItemDetail.Type.EXAMPLE -> Example::class
    }
}

fun LexicalItemDetail.Companion.toType(clazz: KClass<out LexicalItemDetail>): LexicalItemDetail.Type {
    return when (clazz) {
        Forms::class -> LexicalItemDetail.Type.FORMS
        WordTranslations::class -> LexicalItemDetail.Type.WORD_TRANSLATIONS
        Synonyms::class -> LexicalItemDetail.Type.SYNONYMS
        Explanation::class -> LexicalItemDetail.Type.EXPLANATION
        Example::class -> LexicalItemDetail.Type.EXAMPLE
        else -> throw NotImplementedError("Please support LexicalItemDetail subclass: $clazz")
    }
}

val LexicalItemDetail.Type.strRsc: Int
    @StringRes
    get() {
        return when (this) {
            LexicalItemDetail.Type.FORMS -> R.string.general_lexical_item_detail_type_forms
            LexicalItemDetail.Type.WORD_TRANSLATIONS -> R.string.general_lexical_item_detail_type_word_translations
            LexicalItemDetail.Type.SYNONYMS -> R.string.general_lexical_item_detail_type_synonyms
            LexicalItemDetail.Type.EXPLANATION -> R.string.general_lexical_item_detail_type_explanation
            LexicalItemDetail.Type.EXAMPLE -> R.string.general_lexical_item_detail_type_example
        }
    }
