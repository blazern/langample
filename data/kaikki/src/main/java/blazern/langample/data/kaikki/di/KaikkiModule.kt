package blazern.langample.data.kaikki.di

import blazern.langample.data.kaikki.KaikkiClient
import org.koin.dsl.module

fun kaikkiModule() = module {
    single {
        KaikkiClient(
            ktorClientHolder = get(),
        )
    }
}
