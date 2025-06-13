package blazern.langample.data.tatoeba.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class Translation(
    val text: String,
    val lang: String,
    val isDirect: Boolean? = null,
)
