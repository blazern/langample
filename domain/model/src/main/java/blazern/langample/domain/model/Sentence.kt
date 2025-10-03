package blazern.langample.domain.model

data class Sentence(
    val text: String,
    val lang: Lang,
    val source: DataSource,
    val textAccents: Set<TextAccent> = emptySet(),
)
