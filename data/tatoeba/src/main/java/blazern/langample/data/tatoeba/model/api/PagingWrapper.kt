package blazern.langample.data.tatoeba.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PagingWrapper(
    @SerialName("Sentences")
    val sentences: SentencePaging
)
