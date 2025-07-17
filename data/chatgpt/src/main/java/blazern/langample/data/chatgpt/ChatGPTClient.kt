package blazern.langample.data.chatgpt

import arrow.core.Either
import arrow.core.Either.Left
import blazern.langample.core.ktor.KtorClientHolder
import blazern.langample.data.chatgpt.model.api.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ChatGPTClient(
    private val ktorClientHolder: KtorClientHolder,
)  {
    suspend fun request(
        query: String,
        model: String = "gpt-4.1",
    ): Either<Exception, String> {
        val url = "https://api.openai.com/v1/responses"

        val requestBody = mapOf(
            "model" to model,
            "input" to query.replace('\n', ' ').trim()
        )
        val response: ApiResponse = try {
            ktorClientHolder.client.post(url) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.CHATGPT_API_KEY}")
                setBody(requestBody)
            }.body()
        } catch (e: Exception) {
            return Left(e)
        }

        val result = response.output.first().content.first().text
        return Either.Right(result)
    }
}
