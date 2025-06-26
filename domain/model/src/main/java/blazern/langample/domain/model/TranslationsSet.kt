package blazern.langample.domain.model

data class TranslationsSet(
    val original: Sentence,
    val translations: List<Sentence>,
)
