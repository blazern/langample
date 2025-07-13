package blazern.langample.data.panlex.model

import blazern.langample.domain.model.Lang

data class WordData(
    val word: String,
    val lang: Lang,
    val translations: List<WordData> = emptyList(),
    val synonyms: List<WordData> = emptyList(),
)
