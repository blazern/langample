package blazern.langample.data.tatoeba.di

import blazern.langample.data.tatoeba.TatoebaClient
import org.koin.dsl.module

fun tatoebaModule() = module {
    single {
        TatoebaClient(
            get(),
        )
    }
}
