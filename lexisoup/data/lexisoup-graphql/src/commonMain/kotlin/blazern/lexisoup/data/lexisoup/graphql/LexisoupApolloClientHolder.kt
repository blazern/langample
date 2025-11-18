package blazern.lexisoup.data.lexisoup.graphql

import blazern.lexisoup.core.ktor.KtorClientHolder
import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.http.KtorHttpEngine

interface LexisoupApolloClientHolder {
    val client: ApolloClient
}
