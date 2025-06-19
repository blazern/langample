package blazern.langample.data.chatgpt.model.api

import kotlinx.serialization.Serializable

@Serializable
data class OutputMessage(
    val id: String,
    val type: String,
    val status: String,
    val content: List<MessageContent>,
    val role: String
)
