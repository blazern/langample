package blazern.langample.data.panlex.di

import blazern.langample.data.panlex.PanLexClient
import org.koin.dsl.module

fun panLexModule() = module {
    single {
        PanLexClient(
            ktorClientHolder = get(),
        )
    }
}
