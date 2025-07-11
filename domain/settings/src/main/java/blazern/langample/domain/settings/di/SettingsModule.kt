package blazern.langample.domain.settings.di

import blazern.langample.domain.settings.SettingsRepository
import org.koin.dsl.module

fun settingsModule() = module {
    single {
        SettingsRepository()
    }
}
