package blazern.langample.data.panlex.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class DenotationRequest(
    val uid: String,
    val meaning: List<Int> = emptyList(),
)
