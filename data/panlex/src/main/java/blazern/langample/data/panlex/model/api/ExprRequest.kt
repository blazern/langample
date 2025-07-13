package blazern.langample.data.panlex.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ExprRequest(
    val uid: String,
    val txt: String,
    @SerialName("trans_uid") val transUids: List<String>,
    val limit: Int,
)
