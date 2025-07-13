package blazern.langample.data.panlex.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class DenotationResponse(
    val resultType: String,
    val result: List<DenotationEntry> = emptyList(),
    val resultNum: Int,
    val resultMax: Int,
)
