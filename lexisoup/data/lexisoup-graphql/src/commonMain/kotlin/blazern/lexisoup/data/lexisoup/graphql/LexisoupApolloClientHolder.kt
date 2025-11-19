package blazern.lexisoup.data.lexisoup.graphql

import com.apollographql.apollo.ApolloClient

interface LexisoupApolloClientHolder {
    val client: ApolloClient
}
