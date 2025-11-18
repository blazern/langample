package blazern.lexisoup.data.lexisoup.graphql

import blazern.lexisoup.core.ktor.KtorClientHolder
import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.http.KtorHttpEngine

internal class LexisoupApolloClientHolderImpl(
    ktorClientHolder: KtorClientHolder,
) : LexisoupApolloClientHolder {
    override val client = ApolloClient.Builder()
        .serverUrl("https://blazern.me/langample/graphql")
        .httpEngine(KtorHttpEngine(ktorClientHolder.client))
        .build()
}
