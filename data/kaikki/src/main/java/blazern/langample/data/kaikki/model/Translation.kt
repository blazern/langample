package blazern.langample.data.kaikki.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    val sense: String? = null,
    val word: String,
    @SerialName("lang_code") val langCode: String,
    val tags: List<String>? = null
)
