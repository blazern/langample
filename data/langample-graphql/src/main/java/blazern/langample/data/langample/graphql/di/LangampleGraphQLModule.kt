package blazern.langample.data.langample.graphql.di

import blazern.langample.data.langample.graphql.LangampleApolloClientHolder
import org.koin.dsl.module

fun langampleGraphQLModule() = module {
    single {
        LangampleApolloClientHolder(
            ktorClientHolder = get(),
        )
    }
}
