package blazern.langample.domain.model

import blazern.langample.domain.model.LexicalItemDetail.Forms
import kotlin.reflect.KClass

sealed class LexicalItemDetail(
    val type: Type,
    open val source: DataSource,
) {
    data class Forms(
        val text: String,
        override val source: DataSource,
    ) : LexicalItemDetail(Type.FORMS, source)

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
        EXPLANATION,
        EXAMPLE,
    }

    companion object
}

fun LexicalItemDetail.Type.toClass(): KClass<out LexicalItemDetail> {
    return when (this) {
        LexicalItemDetail.Type.FORMS -> LexicalItemDetail.Forms::class
        LexicalItemDetail.Type.EXPLANATION -> LexicalItemDetail.Explanation::class
        LexicalItemDetail.Type.EXAMPLE -> LexicalItemDetail.Example::class
    }
}

fun LexicalItemDetail.Companion.toType(clazz: KClass<out LexicalItemDetail>): LexicalItemDetail.Type {
    return when (clazz) {
        Forms::class -> LexicalItemDetail.Type.FORMS
        LexicalItemDetail.Explanation::class -> LexicalItemDetail.Type.EXPLANATION
        LexicalItemDetail.Example::class -> LexicalItemDetail.Type.EXAMPLE
        else -> throw NotImplementedError("Please support $clazz")
    }
}
