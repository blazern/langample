package blazern.langample.domain.model

import blazern.langample.domain.model.Lang

data class Sentence(
    val text: String,
    val lang: Lang,
)
