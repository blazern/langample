package blazern.langample.core.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClientHolder {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        // TODO: no
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                Log.e("KtorClientHolder", "Ktor error for ${request.url}", exception)
            }
        }
        // TODO: no
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.i("KtorClientHolder", message)
                }
            }
            level = LogLevel.ALL
        }
    }
}
