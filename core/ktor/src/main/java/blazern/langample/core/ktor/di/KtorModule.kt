package blazern.langample.core.ktor.di

import blazern.langample.core.ktor.KtorClientHolder
import org.koin.dsl.module

fun ktorModule() = module {
    single {
        KtorClientHolder()
    }
}