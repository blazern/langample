package blazern.langample.data.kaikki.model

import kotlinx.serialization.Serializable

@Serializable
data class Sense(
    val glosses: List<String> = emptyList(),
    val examples: List<Example> = emptyList()
)
