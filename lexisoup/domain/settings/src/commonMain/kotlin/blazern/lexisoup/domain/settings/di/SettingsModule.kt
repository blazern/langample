package blazern.lexisoup.domain.settings.di

import blazern.lexisoup.domain.settings.SettingsRepository
import org.koin.dsl.module

fun settingsModule() = module {
    single {
        SettingsRepository()
    }
}
