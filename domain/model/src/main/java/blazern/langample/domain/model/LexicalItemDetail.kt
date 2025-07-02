package blazern.langample.domain.model

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
}
