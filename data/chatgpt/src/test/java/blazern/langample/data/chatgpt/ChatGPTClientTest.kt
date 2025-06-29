package blazern.langample.data.chatgpt

import arrow.core.Either
import arrow.core.getOrElse
import blazern.langample.core.ktor.KtorClientHolder
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Test
import kotlin.test.assertEquals

class ChatGPTClientTest {
    private lateinit var response: Either<IOException, String>

    private val mockEngine = MockEngine {
        response.fold(
            { throw it },
            { json ->
                respond(
                    content = ByteReadChannel(json),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        )
    }

    private val ktorHolder = KtorClientHolder(mockEngine)
    private val chatGpt = ChatGPTClient(ktorHolder)

    @Test
    fun `request returns model answer`() = runBlocking {
        setResponse(
            """
            {
              "output": [
                {
                  "content": [
                    { "text": "42 is the answer.", "type": "str" }
                  ],
                  "id": "123",
                  "type": "str",
                  "status": "ok",
                  "role": "assistant"
                }
              ],
              "status": "ok",
              "model": "4o",
              "usage": {
                "input_tokens": 10,
                "output_tokens": 20,
                "total_tokens": 12345
              }
            }
            """.trimIndent()
        )

        val result = chatGpt.request("What is the answer to life?")
            .getOrElse { throw it }
        assertEquals("42 is the answer.", result)
    }

    @Test
    fun `error`() = runBlocking {
        val ioError = IOException("Network offline")
        setResponse(ioError)

        val result = chatGpt.request("Will this fail?")
        assertEquals(ioError, result.leftOrNull()!!.cause)
    }

    private fun setResponse(json: String) {
        response = Either.Right(json)
    }

    private fun setResponse(e: IOException) {
        response = Either.Left(e)
    }
}
