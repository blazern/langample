package blazern.langample.data.lexical_item_details_source.aggregation.di

import blazern.langample.data.kaikki.di.kaikkiModule
import blazern.langample.data.langample.graphql.di.langampleGraphQLModule
import blazern.langample.data.lexical_item_details_source.aggregation.LexicalItemDetailsSourceAggregator
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.kaikki.KaikkiLexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.panlex.PanLexLexicalItemDetailsSource
import blazern.langample.data.lexical_item_details_source.utils.cache.LexicalItemDetailsSourceCacher
import blazern.langample.data.lexical_item_details_source.wortschatz_leipzig.WortschatzLeipzigLexicalItemDetailsSource
import blazern.langample.data.tatoeba.di.tatoebaModule
import blazern.langample.model.lexical_item_details_source.chatgpt.ChatGPTLexicalItemDetailsSource
import blazern.langample.model.lexical_item_details_source.tatoeba.TatoebaLexicalItemDetailsSource
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.di.examplesToolsModule
import org.koin.dsl.bind
import org.koin.dsl.module

fun aggregatingLexicalItemDetailsSourceModules() = listOf(
    tatoebaModule(),
    langampleGraphQLModule(),
    kaikkiModule(),
    examplesToolsModule(),
    module {
        single { LexicalItemDetailsSourceCacher() }

        single {
            TatoebaLexicalItemDetailsSource(
                tatoebaClient = get(),
                formsForExamplesProvider = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single {
            ChatGPTLexicalItemDetailsSource(
                apolloClientHolder = get(),
                cacher = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single {
            KaikkiLexicalItemDetailsSource(
                kaikkiClient = get(),
                cacher = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single {
            PanLexLexicalItemDetailsSource(
                apolloClientHolder = get(),
                cacher = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single {
            WortschatzLeipzigLexicalItemDetailsSource(
                ktorClientHolder = get(),
                formsForExamplesProvider = get(),
            )
        }.bind(LexicalItemDetailsSource::class)

        single {
            LexicalItemDetailsSourceAggregator(
                dataSources = getAll(),
                accentsEnhancerProvider = get(),
            )
        }
    }
)
