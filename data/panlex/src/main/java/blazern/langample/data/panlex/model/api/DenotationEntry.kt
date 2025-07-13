package blazern.langample.data.panlex.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class DenotationEntry(
    val id: Int,
    val expr: Int,
    val meaning: Int,
    val source: Int,
)
