package blazern.langample

import android.app.Application
import blazern.langample.core.ktor.di.ktorModule
import blazern.langample.data.chatgpt.di.chatgptModule
import blazern.langample.data.tatoeba.di.tatoebaModule
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.feature.search_result.di.searchResultModules
import blazern.langample.model.lexical_item_details_source.chatgpt.ChatGPTLexicalItemDetailsSource
import blazern.langample.model.lexical_item_details_source.tatoeba.TatoebaLexicalItemDetailsSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                ktorModule(),
                *searchResultModules().toTypedArray(),
                tatoebaModule(),
                chatgptModule(),
                LexicalItemDetailsSources,
            )
        }
    }
}

private val LexicalItemDetailsSources = module {
    single {
        TatoebaLexicalItemDetailsSource(
            tatoebaClient = get(),
        )
    }.bind(LexicalItemDetailsSource::class)

    single {
        ChatGPTLexicalItemDetailsSource(
            chatGPTClient = get(),
        )
    }.bind(LexicalItemDetailsSource::class)
}
