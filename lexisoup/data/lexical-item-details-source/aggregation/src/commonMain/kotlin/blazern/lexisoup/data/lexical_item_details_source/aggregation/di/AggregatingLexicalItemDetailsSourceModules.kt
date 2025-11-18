package blazern.lexisoup.data.lexical_item_details_source.aggregation.di

import blazern.lexisoup.data.kaikki.di.kaikkiModule
import blazern.lexisoup.data.lexical_item_details_source.aggregation.LexicalItemDetailsSourceAggregator
import blazern.lexisoup.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.lexisoup.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.lexisoup.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSourceImpl
import blazern.lexisoup.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.lexisoup.data.lexical_item_details_source.utils.examples_tools.di.examplesToolsModule
import blazern.lexisoup.data.lexisoup.graphql.di.lexisoupGraphQLModule
import blazern.lexisoup.model.lexical_item_details_source.chatgpt.ChatGPTLexicalItemDetailsSource
import org.koin.dsl.bind
import org.koin.dsl.module

fun aggregatingLexicalItemDetailsSourceModules() = listOf(
//    tatoebaModule(),
    lexisoupGraphQLModule(),
    kaikkiModule(),
    examplesToolsModule(),
    module {
        single { LexicalItemDetailsSourceCacher() }

//        single {
//            TatoebaLexicalItemDetailsSource(
//                tatoebaClient = get(),
//                formsForExamplesProvider = get(),
//            )
//        }.bind(LexicalItemDetailsSource::class)

        single {
            ChatGPTLexicalItemDetailsSource(
                apolloClientHolder = get(),
                cacher = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single<KaikkiLexicalItemDetailsSource> {
            KaikkiLexicalItemDetailsSourceImpl(
                kaikkiClient = get(),
                cacher = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

//        single {
//            PanLexLexicalItemDetailsSource(
//                apolloClientHolder = get(),
//                cacher = get(),
//            )
//        }.bind(LexicalItemDetailsSource::class)

//        single {
//            WortschatzLeipzigLexicalItemDetailsSource(
//                ktorClientHolder = get(),
//                formsForExamplesProvider = get(),
//            )
//        }.bind(LexicalItemDetailsSource::class)

        single {
            LexicalItemDetailsSourceAggregator(
                dataSources = getAll(),
                accentsEnhancerProvider = get(),
            )
        }
    }
)
