package blazern.langample.data.tatoeba.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class Sentence(
    val text: String,
    val lang: String,
    val translations: List<List<Translation>>,
)
