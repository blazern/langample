package blazern.lexisoup

import blazern.lexisoup.domain.settings.di.settingsModule
import blazern.lexisoup.feature.home.di.homeScreenModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            settingsModule(),
            homeScreenModule(),
        )
    }
}
