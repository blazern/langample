package blazern.lexisoup

import blazern.lexisoup.core.ktor.di.ktorModule
import blazern.lexisoup.domain.settings.di.settingsModule
import blazern.lexisoup.feature.home.di.homeScreenModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin() {
    startKoin {
        modules(
            platformModule(),
            ktorModule(),
            settingsModule(),
            homeScreenModule(),
        )
    }
}

internal expect fun platformModule(): Module
