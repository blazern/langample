package blazern.langample.data.panlex.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ExprEntry(
    val id: Int,
    val langvar: Int,
    val txt: String,
    @SerialName("txt_degr") val txtDegr: String? = null,
    @SerialName("trans_expr") val transExpr: Int? = null,
)
