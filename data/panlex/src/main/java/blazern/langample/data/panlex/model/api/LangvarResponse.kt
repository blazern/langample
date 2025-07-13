package blazern.langample.data.panlex.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class LangvarResponse(
    val resultType: String,
    val result: List<LangvarEntry> = emptyList(),
    val resultNum: Int,
    val resultMax: Int,
)
