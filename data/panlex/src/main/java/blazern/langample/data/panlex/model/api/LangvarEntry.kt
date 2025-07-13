package blazern.langample.data.panlex.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LangvarEntry(
    val id: Int,
    @SerialName("lang_code") val langCode: String,
    val uid: String,
)
