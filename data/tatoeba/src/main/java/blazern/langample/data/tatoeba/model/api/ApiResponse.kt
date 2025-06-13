package blazern.langample.data.tatoeba.model.api

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiResponse(
    val paging: PagingWrapper,
    val results: List<Sentence>
)
