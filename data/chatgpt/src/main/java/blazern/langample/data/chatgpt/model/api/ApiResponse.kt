package blazern.langample.data.chatgpt.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val status: String,
    val error: String? = null,
    val model: String,
    val output: List<OutputMessage>,
    val usage: Usage,
)
