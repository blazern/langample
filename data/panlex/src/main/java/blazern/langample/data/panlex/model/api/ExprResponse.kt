package blazern.langample.data.panlex.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class ExprResponse(
    val resultType: String,
    val result: List<ExprEntry> = emptyList(),
    val resultNum: Int,
    val resultMax: Int,
)
