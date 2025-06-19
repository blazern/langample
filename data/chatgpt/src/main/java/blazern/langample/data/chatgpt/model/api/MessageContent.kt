package blazern.langample.data.chatgpt.model.api

import kotlinx.serialization.Serializable

@Serializable
data class MessageContent(
    val type: String,
    val text: String
)
