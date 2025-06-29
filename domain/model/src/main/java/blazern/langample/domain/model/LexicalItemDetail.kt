package blazern.langample.domain.model

sealed class LexicalItemDetail(
    val type: Type,
    open val sources: List<DataSource>,
) {
    data class Forms(
        val text: String,
        override val sources: List<DataSource>,
    ) : LexicalItemDetail(Type.FORMS, sources)

    data class Explanation(
        val text: String,
        override val sources: List<DataSource>,
    ) : LexicalItemDetail(Type.EXPLANATION, sources)

    data class Example(
        val translationsSet: TranslationsSet,
        override val sources: List<DataSource>,
    ) : LexicalItemDetail(Type.EXAMPLE, sources)

    enum class Type {
        FORMS,
        EXPLANATION,
        EXAMPLE,
    }
}
