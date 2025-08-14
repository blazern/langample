package blazern.langample.data.langample.graphql

import blazern.langample.core.ktor.KtorClientHolder
import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.http.KtorHttpEngine

class LangampleApolloClientHolder(
    ktorClientHolder: KtorClientHolder,
) {
    val client = ApolloClient.Builder()
        .serverUrl("https://blazern.me/langample/graphql")
        .httpEngine(KtorHttpEngine(ktorClientHolder.client))
        .build()
}
